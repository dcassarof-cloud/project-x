-- Align ExpoVigia schema with current JPA entities.
-- This migration is intentionally corrective and idempotent for existing dev databases.

-- =====================================================================
-- exhibitor
-- =====================================================================
ALTER TABLE IF EXISTS exhibitor
    ADD COLUMN IF NOT EXISTS updated_at TIMESTAMP WITHOUT TIME ZONE;

UPDATE exhibitor
SET updated_at = COALESCE(updated_at, created_at, NOW())
WHERE updated_at IS NULL;

UPDATE exhibitor
SET cnpj = COALESCE(cnpj, ''),
    corporate_name = COALESCE(corporate_name, ''),
    responsible_name = COALESCE(responsible_name, ''),
    phone = COALESCE(phone, ''),
    email = COALESCE(email, ''),
    area = COALESCE(area, ''),
    created_at = COALESCE(created_at, NOW()),
    updated_at = COALESCE(updated_at, NOW())
WHERE cnpj IS NULL
   OR corporate_name IS NULL
   OR responsible_name IS NULL
   OR phone IS NULL
   OR email IS NULL
   OR area IS NULL
   OR created_at IS NULL
   OR updated_at IS NULL;

ALTER TABLE IF EXISTS exhibitor
    ALTER COLUMN cnpj SET NOT NULL,
    ALTER COLUMN corporate_name SET NOT NULL,
    ALTER COLUMN responsible_name SET NOT NULL,
    ALTER COLUMN phone SET NOT NULL,
    ALTER COLUMN email SET NOT NULL,
    ALTER COLUMN area SET NOT NULL,
    ALTER COLUMN created_at SET NOT NULL,
    ALTER COLUMN updated_at SET NOT NULL;

DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1
        FROM pg_constraint
        WHERE conname = 'uk_exhibitor_cnpj'
          AND conrelid = 'exhibitor'::regclass
    ) THEN
        ALTER TABLE exhibitor ADD CONSTRAINT uk_exhibitor_cnpj UNIQUE (cnpj);
    END IF;
END
$$;

CREATE INDEX IF NOT EXISTS idx_exhibitor_cnpj ON exhibitor (cnpj);

-- =====================================================================
-- vehicle
-- =====================================================================
ALTER TABLE IF EXISTS vehicle
    ADD COLUMN IF NOT EXISTS updated_at TIMESTAMP WITHOUT TIME ZONE;

UPDATE vehicle
SET updated_at = COALESCE(updated_at, created_at, NOW())
WHERE updated_at IS NULL;

UPDATE vehicle v
SET plate = COALESCE(v.plate, ''),
    exhibitor_id = COALESCE(v.exhibitor_id, (SELECT MIN(id) FROM exhibitor)),
    created_at = COALESCE(v.created_at, NOW()),
    updated_at = COALESCE(v.updated_at, NOW())
WHERE v.plate IS NULL
   OR v.exhibitor_id IS NULL
   OR v.created_at IS NULL
   OR v.updated_at IS NULL;

ALTER TABLE IF EXISTS vehicle
    ALTER COLUMN plate SET NOT NULL,
    ALTER COLUMN exhibitor_id SET NOT NULL,
    ALTER COLUMN created_at SET NOT NULL,
    ALTER COLUMN updated_at SET NOT NULL;

DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1
        FROM pg_constraint
        WHERE conname = 'uk_vehicle_plate'
          AND conrelid = 'vehicle'::regclass
    ) THEN
        ALTER TABLE vehicle ADD CONSTRAINT uk_vehicle_plate UNIQUE (plate);
    END IF;
END
$$;

DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1
        FROM pg_constraint
        WHERE conname = 'fk_vehicle_exhibitor'
          AND conrelid = 'vehicle'::regclass
    ) THEN
        ALTER TABLE vehicle
            ADD CONSTRAINT fk_vehicle_exhibitor
            FOREIGN KEY (exhibitor_id) REFERENCES exhibitor (id);
    END IF;
END
$$;

CREATE INDEX IF NOT EXISTS idx_vehicle_plate ON vehicle (plate);
CREATE INDEX IF NOT EXISTS idx_vehicle_exhibitor_id ON vehicle (exhibitor_id);

-- =====================================================================
-- vehicle_access
-- =====================================================================
ALTER TABLE IF EXISTS vehicle_access
    ADD COLUMN IF NOT EXISTS updated_at TIMESTAMP WITHOUT TIME ZONE;

UPDATE vehicle_access
SET updated_at = COALESCE(updated_at, created_at, NOW())
WHERE updated_at IS NULL;

UPDATE vehicle_access
SET plate = COALESCE(plate, ''),
    gate = COALESCE(gate, ''),
    entry_time = COALESCE(entry_time, NOW()),
    status = COALESCE(status, 'IN_PATIO'),
    created_at = COALESCE(created_at, NOW()),
    updated_at = COALESCE(updated_at, NOW())
WHERE plate IS NULL
   OR gate IS NULL
   OR entry_time IS NULL
   OR status IS NULL
   OR created_at IS NULL
   OR updated_at IS NULL;

ALTER TABLE IF EXISTS vehicle_access
    ALTER COLUMN vehicle_id SET NOT NULL,
    ALTER COLUMN plate SET NOT NULL,
    ALTER COLUMN gate SET NOT NULL,
    ALTER COLUMN entry_time SET NOT NULL,
    ALTER COLUMN status SET NOT NULL,
    ALTER COLUMN created_at SET NOT NULL,
    ALTER COLUMN updated_at SET NOT NULL;

DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1
        FROM pg_constraint
        WHERE conname = 'fk_vehicle_access_vehicle'
          AND conrelid = 'vehicle_access'::regclass
    ) THEN
        ALTER TABLE vehicle_access
            ADD CONSTRAINT fk_vehicle_access_vehicle
            FOREIGN KEY (vehicle_id) REFERENCES vehicle (id);
    END IF;
END
$$;

CREATE INDEX IF NOT EXISTS idx_vehicle_access_plate ON vehicle_access (plate);
CREATE INDEX IF NOT EXISTS idx_vehicle_access_status ON vehicle_access (status);
CREATE INDEX IF NOT EXISTS idx_vehicle_access_vehicle_id ON vehicle_access (vehicle_id);
CREATE INDEX IF NOT EXISTS idx_vehicle_access_entry_time ON vehicle_access (entry_time);

-- =====================================================================
-- alert_log
-- =====================================================================
CREATE TABLE IF NOT EXISTS alert_log (
    id BIGSERIAL PRIMARY KEY,
    vehicle_id BIGINT,
    plate VARCHAR(10) NOT NULL,
    phone VARCHAR(20) NOT NULL,
    message VARCHAR(500) NOT NULL,
    type VARCHAR(30) NOT NULL,
    sent_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT NOW()
);

ALTER TABLE IF EXISTS alert_log
    ADD COLUMN IF NOT EXISTS vehicle_id BIGINT,
    ADD COLUMN IF NOT EXISTS plate VARCHAR(10),
    ADD COLUMN IF NOT EXISTS phone VARCHAR(20),
    ADD COLUMN IF NOT EXISTS message VARCHAR(500),
    ADD COLUMN IF NOT EXISTS type VARCHAR(30),
    ADD COLUMN IF NOT EXISTS sent_at TIMESTAMP WITHOUT TIME ZONE,
    ADD COLUMN IF NOT EXISTS created_at TIMESTAMP WITHOUT TIME ZONE;

UPDATE alert_log
SET plate = COALESCE(plate, ''),
    phone = COALESCE(phone, ''),
    message = COALESCE(message, ''),
    type = COALESCE(type, 'OVERSTAY'),
    sent_at = COALESCE(sent_at, NOW()),
    created_at = COALESCE(created_at, NOW())
WHERE plate IS NULL
   OR phone IS NULL
   OR message IS NULL
   OR type IS NULL
   OR sent_at IS NULL
   OR created_at IS NULL;

ALTER TABLE IF EXISTS alert_log
    ALTER COLUMN plate SET NOT NULL,
    ALTER COLUMN phone SET NOT NULL,
    ALTER COLUMN message SET NOT NULL,
    ALTER COLUMN type SET NOT NULL,
    ALTER COLUMN sent_at SET NOT NULL,
    ALTER COLUMN created_at SET NOT NULL;

DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1
        FROM pg_constraint
        WHERE conname = 'fk_alert_log_vehicle'
          AND conrelid = 'alert_log'::regclass
    ) THEN
        ALTER TABLE alert_log
            ADD CONSTRAINT fk_alert_log_vehicle
            FOREIGN KEY (vehicle_id) REFERENCES vehicle (id);
    END IF;
END
$$;

CREATE INDEX IF NOT EXISTS idx_alert_log_plate ON alert_log (plate);
CREATE INDEX IF NOT EXISTS idx_alert_log_type ON alert_log (type);
CREATE INDEX IF NOT EXISTS idx_alert_log_vehicle_id ON alert_log (vehicle_id);
CREATE INDEX IF NOT EXISTS idx_alert_log_sent_at ON alert_log (sent_at);

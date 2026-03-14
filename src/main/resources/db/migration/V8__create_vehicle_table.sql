-- Extend vehicle table to support direct vehicle registration payload.
ALTER TABLE IF EXISTS vehicle
    ADD COLUMN IF NOT EXISTS company_name VARCHAR(200),
    ADD COLUMN IF NOT EXISTS responsible_name VARCHAR(200),
    ADD COLUMN IF NOT EXISTS phone VARCHAR(30),
    ADD COLUMN IF NOT EXISTS gate VARCHAR(50),
    ADD COLUMN IF NOT EXISTS status VARCHAR(50);

-- Keep backwards compatibility with existing vehicle records linked to exhibitor.
ALTER TABLE IF EXISTS vehicle
    ALTER COLUMN exhibitor_id DROP NOT NULL;

ALTER TABLE IF EXISTS vehicle
    DROP COLUMN IF EXISTS updated_at;


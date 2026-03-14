-- Create vehicle table to keep authorized exhibitor vehicles.
CREATE TABLE IF NOT EXISTS vehicle (
    id BIGSERIAL PRIMARY KEY,
    plate VARCHAR(10) NOT NULL,
    exhibitor_id BIGINT NOT NULL,
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT NOW(),
    CONSTRAINT uk_vehicle_plate UNIQUE (plate),
    CONSTRAINT fk_vehicle_exhibitor FOREIGN KEY (exhibitor_id) REFERENCES exhibitor (id)
);

CREATE INDEX IF NOT EXISTS idx_vehicle_plate ON vehicle (plate);
CREATE INDEX IF NOT EXISTS idx_vehicle_exhibitor_id ON vehicle (exhibitor_id);

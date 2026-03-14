-- Create vehicle access event table for entry and exit control.
CREATE TABLE IF NOT EXISTS vehicle_access (
    id BIGSERIAL PRIMARY KEY,
    vehicle_id BIGINT NOT NULL,
    plate VARCHAR(10) NOT NULL,
    gate VARCHAR(50) NOT NULL,
    entry_time TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    exit_time TIMESTAMP WITHOUT TIME ZONE,
    status VARCHAR(30) NOT NULL,
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT NOW(),
    CONSTRAINT fk_vehicle_access_vehicle FOREIGN KEY (vehicle_id) REFERENCES vehicle (id)
);

CREATE INDEX IF NOT EXISTS idx_vehicle_access_plate ON vehicle_access (plate);
CREATE INDEX IF NOT EXISTS idx_vehicle_access_status ON vehicle_access (status);
CREATE INDEX IF NOT EXISTS idx_vehicle_access_vehicle_id ON vehicle_access (vehicle_id);
CREATE INDEX IF NOT EXISTS idx_vehicle_access_entry_time ON vehicle_access (entry_time);

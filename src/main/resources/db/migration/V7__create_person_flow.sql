-- Create table for people flow control (entries and exits).
CREATE TABLE IF NOT EXISTS person_flow (
    id BIGSERIAL PRIMARY KEY,
    type VARCHAR(10) NOT NULL,
    quantity INTEGER NOT NULL,
    gate VARCHAR(50) NOT NULL,
    recorded_at TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT NOW(),
    source VARCHAR(100) NOT NULL,
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT NOW(),
    CONSTRAINT chk_person_flow_type CHECK (type IN ('ENTRY', 'EXIT')),
    CONSTRAINT chk_person_flow_quantity CHECK (quantity > 0)
);

CREATE INDEX IF NOT EXISTS idx_person_flow_recorded_at ON person_flow (recorded_at);
CREATE INDEX IF NOT EXISTS idx_person_flow_type ON person_flow (type);
CREATE INDEX IF NOT EXISTS idx_person_flow_gate ON person_flow (gate);

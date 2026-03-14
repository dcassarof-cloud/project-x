-- Create alert log table for warning and overtime notifications.
CREATE TABLE IF NOT EXISTS alert_log (
    id BIGSERIAL PRIMARY KEY,
    vehicle_id BIGINT,
    plate VARCHAR(10) NOT NULL,
    phone VARCHAR(20) NOT NULL,
    message VARCHAR(500) NOT NULL,
    type VARCHAR(30) NOT NULL,
    sent_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT NOW(),
    CONSTRAINT fk_alert_log_vehicle FOREIGN KEY (vehicle_id) REFERENCES vehicle (id)
);

CREATE INDEX IF NOT EXISTS idx_alert_log_plate ON alert_log (plate);
CREATE INDEX IF NOT EXISTS idx_alert_log_type ON alert_log (type);
CREATE INDEX IF NOT EXISTS idx_alert_log_vehicle_id ON alert_log (vehicle_id);
CREATE INDEX IF NOT EXISTS idx_alert_log_sent_at ON alert_log (sent_at);

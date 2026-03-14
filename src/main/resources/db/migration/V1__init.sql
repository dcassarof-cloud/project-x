-- Initial schema for ExpoVigia bootstrap
-- This table validates Flyway integration and keeps simple app metadata.
CREATE TABLE IF NOT EXISTS app_metadata (
    id BIGSERIAL PRIMARY KEY,
    key_name VARCHAR(100) NOT NULL UNIQUE,
    key_value VARCHAR(255) NOT NULL,
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT NOW()
);

INSERT INTO app_metadata (key_name, key_value)
VALUES ('schema_version', '1')
ON CONFLICT (key_name) DO NOTHING;

-- Create exhibitor table for ExpoVigia domain model.
CREATE TABLE IF NOT EXISTS exhibitor (
    id BIGSERIAL PRIMARY KEY,
    cnpj VARCHAR(14) NOT NULL,
    corporate_name VARCHAR(255) NOT NULL,
    responsible_name VARCHAR(255) NOT NULL,
    phone VARCHAR(20) NOT NULL,
    email VARCHAR(255) NOT NULL,
    area VARCHAR(100) NOT NULL,
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT NOW(),
    CONSTRAINT uk_exhibitor_cnpj UNIQUE (cnpj)
);

CREATE INDEX IF NOT EXISTS idx_exhibitor_cnpj ON exhibitor (cnpj);

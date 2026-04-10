3CREATE TABLE IF NOT EXISTS promoters(
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    email VARCHAR(255) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    name VARCHAR(100) NOT NULL,
    description VARCHAR(600),
    city VARCHAR(100) NOT NULL,
    country VARCHAR(100) NOT NULL,
    address VARCHAR(255),
    logo_asset_id UUID,
    phone VARCHAR(50),
    website VARCHAR(255),
    street VARCHAR(255),
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    active BOOLEAN NOT NULL DEFAULT false,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS promoter_genres(
    promoter_id UUID NOT NULL REFERENCES promoters (id) ON DELETE CASCADE,
    genre VARCHAR(50) NOT NULL,
    PRIMARY KEY (promoter_id, genre)
);

CREATE INDEX IF NOT EXISTS idx_promoters_status        ON promoters (status);
CREATE INDEX IF NOT EXISTS idx_promoters_active        ON promoters (active);
CREATE INDEX IF NOT EXISTS idx_promoter_genres_genre   ON promoter_genres (genre);


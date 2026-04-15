-- ─────────────────────────────────────────────────────────────────────────
-- V3: promoters + promoter_genres
--
-- Promoter aggregate:
--   id (propio), account_id (FK → accounts), email, name, description,
--   address (street + city + country), logoAssetId, musicalGenres,
--   createdAt, phone, website
--   + updatedAt / version (Hibernate infra)
--
-- account_id es la FK que relaciona el perfil con la cuenta
-- AccountStatus vive exclusivamente en accounts
-- ─────────────────────────────────────────────────────────────────────────

CREATE TABLE promoters (
    id              UUID         NOT NULL,
    account_id      UUID         NOT NULL,
    email           VARCHAR(255) NOT NULL,
    name            VARCHAR(100) NOT NULL,
    description     VARCHAR(600),
    logo_asset_id   VARCHAR(255),
    street          VARCHAR(255) NOT NULL,
    city            VARCHAR(100) NOT NULL,
    country         VARCHAR(100) NOT NULL,
    phone           VARCHAR(50),
    website         VARCHAR(255),
    created_at      TIMESTAMPTZ  NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMPTZ  NOT NULL DEFAULT CURRENT_TIMESTAMP,
    version         BIGINT       NOT NULL DEFAULT 0,

    CONSTRAINT pk_promoters             PRIMARY KEY (id),
    CONSTRAINT uq_promoters_email       UNIQUE (email),
    CONSTRAINT uq_promoters_account     UNIQUE (account_id),
    CONSTRAINT fk_promoters_account     FOREIGN KEY (account_id) REFERENCES accounts (id) ON DELETE CASCADE
);

CREATE TABLE promoter_genres (
    promoter_id UUID        NOT NULL,
    genre       VARCHAR(50) NOT NULL,

    CONSTRAINT pk_promoter_genres           PRIMARY KEY (promoter_id, genre),
    CONSTRAINT fk_promoter_genres_promoter  FOREIGN KEY (promoter_id) REFERENCES promoters (id) ON DELETE CASCADE
);

CREATE INDEX idx_promoters_account_id   ON promoters (account_id);
CREATE INDEX idx_promoters_city         ON promoters (city);
CREATE INDEX idx_promoter_genres_genre  ON promoter_genres (genre);

-- ─────────────────────────────────────────────────────────────────────────
-- V2: users + user_roles + user_genres
--
-- User aggregate:
--   id (propio), account_id (FK → accounts), email, name, description,
--   city, avatarAssetId, musicGenres, tier, loyaltyPoints, roles, createdAt
--   + updatedAt / version (Hibernate infra)
--
-- account_id es la FK que relaciona el perfil con la cuenta
-- AccountStatus vive exclusivamente en accounts
-- UserTier enum: FREE, PREMIUM
-- ─────────────────────────────────────────────────────────────────────────

CREATE TABLE users (
    id               UUID         NOT NULL,
    account_id       UUID         NOT NULL,
    email            VARCHAR(255) NOT NULL,
    name             VARCHAR(100) NOT NULL,
    description      VARCHAR(600),
    avatar_asset_id  VARCHAR(255),
    city             VARCHAR(100) NOT NULL,
    tier             VARCHAR(20)  NOT NULL DEFAULT 'FREE',
    loyalty_points   INTEGER      NOT NULL DEFAULT 0,
    created_at       TIMESTAMPTZ  NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at       TIMESTAMPTZ  NOT NULL DEFAULT CURRENT_TIMESTAMP,
    version          BIGINT       NOT NULL DEFAULT 0,

    CONSTRAINT pk_users               PRIMARY KEY (id),
    CONSTRAINT uq_users_email         UNIQUE (email),
    CONSTRAINT uq_users_account       UNIQUE (account_id),
    CONSTRAINT fk_users_account       FOREIGN KEY (account_id) REFERENCES accounts (id) ON DELETE CASCADE,
    CONSTRAINT chk_users_tier         CHECK (tier IN ('FREE', 'PREMIUM')),
    CONSTRAINT chk_users_loyalty      CHECK (loyalty_points >= 0)
);

CREATE TABLE user_roles (
    user_id UUID        NOT NULL,
    role    VARCHAR(50) NOT NULL,

    CONSTRAINT pk_user_roles        PRIMARY KEY (user_id, role),
    CONSTRAINT fk_user_roles_user   FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
    CONSTRAINT chk_user_roles_role  CHECK (role IN ('USER', 'ARTIST', 'PROMOTER', 'PROMOTER_PENDING', 'ADMIN'))
);

CREATE TABLE user_genres (
    user_id UUID        NOT NULL,
    genre   VARCHAR(50) NOT NULL,

    CONSTRAINT pk_user_genres       PRIMARY KEY (user_id, genre),
    CONSTRAINT fk_user_genres_user  FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
);

CREATE INDEX idx_users_email      ON users (email);
CREATE INDEX idx_users_account_id ON users (account_id);
CREATE INDEX idx_users_tier       ON users (tier);

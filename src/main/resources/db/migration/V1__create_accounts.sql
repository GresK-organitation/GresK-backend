-- ─────────────────────────────────────────────────────────────────────────
-- V1: accounts + account_roles
--
-- Account aggregate:
--   id, email, passwordHash, roles (Set<Role>), status, createdAt
--   + updatedAt / version (Hibernate infra)
--
-- Role enum: USER, ARTIST, PROMOTER, PROMOTER_PENDING, ADMIN
-- AccountStatus enum: PENDING, ACTIVE, SUSPENDED, DELETED
-- ─────────────────────────────────────────────────────────────────────────

CREATE TABLE accounts (
    id            UUID         NOT NULL,
    email         VARCHAR(255) NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    status        VARCHAR(20)  NOT NULL DEFAULT 'PENDING',
    created_at    TIMESTAMPTZ  NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at    TIMESTAMPTZ  NOT NULL DEFAULT CURRENT_TIMESTAMP,
    version       BIGINT       NOT NULL DEFAULT 0,

    CONSTRAINT pk_accounts        PRIMARY KEY (id),
    CONSTRAINT uq_accounts_email  UNIQUE (email),
    CONSTRAINT chk_accounts_status CHECK (status IN ('PENDING', 'ACTIVE', 'SUSPENDED', 'DELETED'))
);

CREATE TABLE account_roles (
    account_id UUID        NOT NULL,
    role       VARCHAR(50) NOT NULL,

    CONSTRAINT pk_account_roles            PRIMARY KEY (account_id, role),
    CONSTRAINT fk_account_roles_account    FOREIGN KEY (account_id) REFERENCES accounts (id) ON DELETE CASCADE,
    CONSTRAINT chk_account_roles_role      CHECK (role IN ('USER', 'ARTIST', 'PROMOTER', 'PROMOTER_PENDING', 'ADMIN'))
);

CREATE INDEX idx_accounts_email  ON accounts (email);
CREATE INDEX idx_accounts_status ON accounts (status);

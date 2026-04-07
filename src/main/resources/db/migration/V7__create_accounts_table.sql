CREATE TABLE IF NOT EXISTS accounts (
    id            UUID         NOT NULL,
    email         VARCHAR(255) NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    status        VARCHAR(20)  NOT NULL DEFAULT 'PENDING',
    created_at    TIMESTAMPTZ  NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT pk_accounts PRIMARY KEY (id),
    CONSTRAINT uq_accounts_email UNIQUE (email),
    CONSTRAINT chk_accounts_status CHECK (status IN ('PENDING', 'ACTIVE', 'SUSPENDED', 'DELETED'))
);

CREATE TABLE IF NOT EXISTS account_roles (
    account_id UUID        NOT NULL,
    role       VARCHAR(50) NOT NULL,

    CONSTRAINT pk_account_roles PRIMARY KEY (account_id, role),
    CONSTRAINT fk_account_roles_account FOREIGN KEY (account_id) REFERENCES accounts (id) ON DELETE CASCADE,
    CONSTRAINT chk_account_roles_role CHECK (role IN ('USER', 'PROMOTER', 'ARTIST'))
);

CREATE INDEX IF NOT EXISTS idx_accounts_email  ON accounts (email);
CREATE INDEX IF NOT EXISTS idx_accounts_status ON accounts (status);

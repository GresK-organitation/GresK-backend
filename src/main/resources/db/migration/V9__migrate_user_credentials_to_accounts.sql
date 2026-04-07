-- Migrates existing user credentials into the accounts table.
-- Users table already has status values compatible with AccountStatus.
-- Drops password column from users as credentials now live in accounts.

INSERT INTO accounts (id, email, password_hash, status, created_at)
SELECT id,
       email,
       password,
       status,
       created_at
FROM users
ON CONFLICT (email) DO NOTHING;

INSERT INTO account_roles (account_id, role)
SELECT id, 'USER'
FROM users
ON CONFLICT DO NOTHING;

ALTER TABLE users DROP COLUMN IF EXISTS password;

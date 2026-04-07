-- Migrates existing promoter credentials into the accounts table.
-- Adds account_id column to promoters to link profiles to their account row.
-- Drops password_hash from promoters as credentials now live in accounts.

ALTER TABLE promoters ADD COLUMN IF NOT EXISTS account_id UUID;

INSERT INTO accounts (id, email, password_hash, status, created_at)
SELECT id,
       email,
       password_hash,
       CASE status
           WHEN 'ACTIVE'    THEN 'ACTIVE'
           WHEN 'SUSPENDED' THEN 'SUSPENDED'
           ELSE 'PENDING'
       END,
       created_at
FROM promoters
ON CONFLICT (email) DO NOTHING;

INSERT INTO account_roles (account_id, role)
SELECT id, 'PROMOTER'
FROM promoters
ON CONFLICT DO NOTHING;

UPDATE promoters p
SET account_id = a.id
FROM accounts a
WHERE p.email = a.email;

ALTER TABLE promoters DROP COLUMN IF EXISTS password_hash;

ALTER TABLE promoters
    ADD COLUMN IF NOT EXISTS logo_asset_id VARCHAR(255);

-- Backfill from legacy logo_url by extracting the last path segment.
-- Example:
-- https://.../image/upload/.../main-sample.png  -> main-sample.png
UPDATE promoters
SET logo_asset_id = NULLIF(regexp_replace(logo_url, '^.*/', ''), '')
WHERE logo_asset_id IS NULL
  AND logo_url IS NOT NULL
  AND logo_url <> '';

ALTER TABLE promoters
    DROP COLUMN IF EXISTS logo_url;


-- ─────────────────────────────────────────────────────────────────────────
-- V21: epk_asset + epk_asset_version + epk_share_link
--
-- EpkAsset (aggregate independiente, NO pertenece a Artist):
--   id, artist_id (FK→artists), promoter_id (FK→promoters), type, label,
--   archived, createdAt/updatedAt + version (optimistic locking)
-- EpkAssetVersion (@ElementCollection, embebido, sin id propio):
--   versionNumber, storedFile (AssetId Cloudinary raw), fileName, mimeType,
--   fileSizeBytes, uploadedByUserId, uploadedAt
-- EpkShareLink (entidad separada, con repositorio propio):
--   id, epk_asset_id (FK→epk_asset), version_number, promoter_id,
--   token (único, opaco), expires_at, max_downloads, download_count,
--   revoked, created_by_user_id, created_at
--
-- EpkAssetType enum: DOSSIER, LOGO_VECTOR, PRESS_PHOTO, RIDER
-- ─────────────────────────────────────────────────────────────────────────

CREATE TABLE epk_asset (
    id           UUID         NOT NULL,
    artist_id    UUID         NOT NULL,
    promoter_id  UUID         NOT NULL,
    type         VARCHAR(20)  NOT NULL,
    label        VARCHAR(150) NOT NULL,
    archived     BOOLEAN      NOT NULL DEFAULT false,
    created_at   TIMESTAMPTZ  NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at   TIMESTAMPTZ  NOT NULL DEFAULT CURRENT_TIMESTAMP,
    version      BIGINT       NOT NULL DEFAULT 0,

    CONSTRAINT pk_epk_asset          PRIMARY KEY (id),
    CONSTRAINT fk_epk_asset_artist   FOREIGN KEY (artist_id)   REFERENCES artists   (id) ON DELETE CASCADE,
    CONSTRAINT fk_epk_asset_promoter FOREIGN KEY (promoter_id) REFERENCES promoters (id) ON DELETE CASCADE,
    CONSTRAINT chk_epk_asset_type    CHECK (type IN ('DOSSIER', 'LOGO_VECTOR', 'PRESS_PHOTO', 'RIDER'))
);

CREATE INDEX idx_epk_asset_artist_id   ON epk_asset (artist_id);
CREATE INDEX idx_epk_asset_promoter_id ON epk_asset (promoter_id);
CREATE INDEX idx_epk_asset_artist_type ON epk_asset (artist_id, type);

-- ── epk_asset_version (@ElementCollection) ────────────────────────────────
CREATE TABLE epk_asset_version (
    epk_asset_id        UUID          NOT NULL,
    version_number       INTEGER       NOT NULL,
    stored_file          VARCHAR(2048) NOT NULL,
    file_name            VARCHAR(255)  NOT NULL,
    mime_type            VARCHAR(150)  NOT NULL,
    file_size_bytes      BIGINT        NOT NULL,
    uploaded_by_user_id  VARCHAR(100)  NOT NULL,
    uploaded_at          TIMESTAMPTZ   NOT NULL,

    CONSTRAINT pk_epk_asset_version PRIMARY KEY (epk_asset_id, version_number),
    CONSTRAINT fk_epk_version_asset FOREIGN KEY (epk_asset_id) REFERENCES epk_asset (id) ON DELETE CASCADE,
    CONSTRAINT chk_epk_version_size CHECK (file_size_bytes >= 0)
);

-- ── epk_share_link ─────────────────────────────────────────────────────────
CREATE TABLE epk_share_link (
    id                  UUID        NOT NULL,
    epk_asset_id        UUID        NOT NULL,
    version_number      INTEGER     NOT NULL,
    promoter_id         UUID        NOT NULL,
    token               VARCHAR(36) NOT NULL,
    expires_at          TIMESTAMPTZ NOT NULL,
    max_downloads       INTEGER,
    download_count      INTEGER     NOT NULL DEFAULT 0,
    revoked             BOOLEAN     NOT NULL DEFAULT false,
    created_by_user_id  VARCHAR(100) NOT NULL,
    created_at          TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT pk_epk_share_link          PRIMARY KEY (id),
    CONSTRAINT uq_epk_share_link_token    UNIQUE (token),
    CONSTRAINT fk_epk_share_link_asset    FOREIGN KEY (epk_asset_id) REFERENCES epk_asset (id) ON DELETE CASCADE,
    CONSTRAINT fk_epk_share_link_promoter FOREIGN KEY (promoter_id) REFERENCES promoters (id) ON DELETE CASCADE,
    CONSTRAINT chk_epk_share_link_maxdl   CHECK (max_downloads IS NULL OR max_downloads > 0)
);

CREATE INDEX idx_epk_share_link_asset_id ON epk_share_link (epk_asset_id);
CREATE INDEX idx_epk_share_link_token    ON epk_share_link (token);
CREATE INDEX idx_epk_share_link_expired  ON epk_share_link (expires_at) WHERE revoked = false;

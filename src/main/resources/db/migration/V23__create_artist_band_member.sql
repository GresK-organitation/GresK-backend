-- ─────────────────────────────────────────────────────────────────────────
-- V23: band_member + band_member_document
--
-- BandMember: miembro de la banda (para gestión de visados/documentación de gira)
--   id, artist_id (FK→artists), promoter_id (FK→promoters), name, role_in_band,
--   active, createdAt/updatedAt + version (optimistic locking)
-- IdentityDocument (@ElementCollection, embebido, sin id propio):
--   type, document_number, issuing_country, expiry_date (nullable)
--
-- IdentityDocumentType enum: NATIONAL_ID, PASSPORT, WORK_VISA, RESIDENCE_PERMIT
-- ─────────────────────────────────────────────────────────────────────────

CREATE TABLE band_member (
    id            UUID         NOT NULL,
    artist_id     UUID         NOT NULL,
    promoter_id   UUID         NOT NULL,
    name          VARCHAR(100) NOT NULL,
    role_in_band  VARCHAR(100) NOT NULL,
    active        BOOLEAN      NOT NULL DEFAULT true,
    created_at    TIMESTAMPTZ  NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at    TIMESTAMPTZ  NOT NULL DEFAULT CURRENT_TIMESTAMP,
    version       BIGINT       NOT NULL DEFAULT 0,

    CONSTRAINT pk_band_member          PRIMARY KEY (id),
    CONSTRAINT fk_band_member_artist   FOREIGN KEY (artist_id)   REFERENCES artists   (id) ON DELETE CASCADE,
    CONSTRAINT fk_band_member_promoter FOREIGN KEY (promoter_id) REFERENCES promoters (id) ON DELETE CASCADE
);

CREATE INDEX idx_band_member_artist_id   ON band_member (artist_id);
CREATE INDEX idx_band_member_promoter_id ON band_member (promoter_id);
CREATE INDEX idx_band_member_active      ON band_member (active) WHERE active = true;

-- ── band_member_document (@ElementCollection) ─────────────────────────────
CREATE TABLE band_member_document (
    band_member_id   UUID         NOT NULL,
    doc_type         VARCHAR(20)  NOT NULL,
    document_number  VARCHAR(100) NOT NULL,
    issuing_country  VARCHAR(100) NOT NULL,
    expiry_date      DATE,

    CONSTRAINT fk_band_member_doc_member FOREIGN KEY (band_member_id) REFERENCES band_member (id) ON DELETE CASCADE,
    CONSTRAINT chk_band_member_doc_type  CHECK (doc_type IN ('NATIONAL_ID', 'PASSPORT', 'WORK_VISA', 'RESIDENCE_PERMIT'))
);

CREATE INDEX idx_band_member_doc_member_id ON band_member_document (band_member_id);
CREATE INDEX idx_band_member_doc_expiry    ON band_member_document (expiry_date) WHERE expiry_date IS NOT NULL;

-- ─────────────────────────────────────────────────────────────────────────
-- V22: roster_member
--
-- RosterMember: contacto profesional del artista (booking, management, etc.)
--   id, artist_id (FK→artists), promoter_id (FK→promoters), name, role,
--   phone, email (nullable), billing_* (BillingDetails aplanado, texto libre),
--   is_primary, active, createdAt/updatedAt + version (optimistic locking)
--
-- ContactRole enum: BOOKING_AGENT, MANAGEMENT, TOUR_MANAGER, PR, LEGAL, OTHER
-- ─────────────────────────────────────────────────────────────────────────

CREATE TABLE roster_member (
    id                  UUID         NOT NULL,
    artist_id           UUID         NOT NULL,
    promoter_id         UUID         NOT NULL,
    name                VARCHAR(100) NOT NULL,
    role                VARCHAR(20)  NOT NULL,
    phone               VARCHAR(30)  NOT NULL,
    email               VARCHAR(255),
    billing_legal_name  VARCHAR(255),
    billing_tax_id      VARCHAR(255),
    billing_address     VARCHAR(255),
    billing_iban        VARCHAR(255),
    is_primary          BOOLEAN      NOT NULL DEFAULT false,
    active              BOOLEAN      NOT NULL DEFAULT true,
    created_at          TIMESTAMPTZ  NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at          TIMESTAMPTZ  NOT NULL DEFAULT CURRENT_TIMESTAMP,
    version             BIGINT       NOT NULL DEFAULT 0,

    CONSTRAINT pk_roster_member          PRIMARY KEY (id),
    CONSTRAINT fk_roster_member_artist   FOREIGN KEY (artist_id)   REFERENCES artists   (id) ON DELETE CASCADE,
    CONSTRAINT fk_roster_member_promoter FOREIGN KEY (promoter_id) REFERENCES promoters (id) ON DELETE CASCADE,
    CONSTRAINT chk_roster_member_role    CHECK (role IN ('BOOKING_AGENT', 'MANAGEMENT', 'TOUR_MANAGER', 'PR', 'LEGAL', 'OTHER'))
);

CREATE INDEX idx_roster_member_artist_id   ON roster_member (artist_id);
CREATE INDEX idx_roster_member_promoter_id ON roster_member (promoter_id);

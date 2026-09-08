-- ─────────────────────────────────────────────────────────────────────────
-- V24: document_expiry_alert
--
-- DocumentExpiryAlert: notificación in-app de documento próximo a caducar
--   (patrón calcado de rider_alerts / RiderAlert)
-- ─────────────────────────────────────────────────────────────────────────

CREATE TABLE document_expiry_alert (
    id              UUID        NOT NULL,
    promoter_id     UUID        NOT NULL,
    artist_id       UUID        NOT NULL,
    band_member_id  UUID        NOT NULL,
    document_type   VARCHAR(20) NOT NULL,
    expiry_date     DATE        NOT NULL,
    message         TEXT        NOT NULL,
    read            BOOLEAN     NOT NULL DEFAULT false,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT pk_document_expiry_alert        PRIMARY KEY (id),
    CONSTRAINT fk_doc_expiry_alert_promoter    FOREIGN KEY (promoter_id)    REFERENCES promoters   (id) ON DELETE CASCADE,
    CONSTRAINT fk_doc_expiry_alert_artist      FOREIGN KEY (artist_id)      REFERENCES artists     (id) ON DELETE CASCADE,
    CONSTRAINT fk_doc_expiry_alert_band_member FOREIGN KEY (band_member_id) REFERENCES band_member (id) ON DELETE CASCADE,
    CONSTRAINT chk_doc_expiry_alert_type       CHECK (document_type IN ('NATIONAL_ID', 'PASSPORT', 'WORK_VISA', 'RESIDENCE_PERMIT'))
);

CREATE INDEX idx_doc_expiry_alert_promoter_id ON document_expiry_alert (promoter_id);
CREATE INDEX idx_doc_expiry_alert_unread
    ON document_expiry_alert (promoter_id, created_at DESC)
   WHERE read = false;
CREATE INDEX idx_doc_expiry_alert_member_type_unread
    ON document_expiry_alert (band_member_id, document_type)
   WHERE read = false;

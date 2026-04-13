-- ─────────────────────────────────────────────────────────────────────────
-- V5: tickets
--
-- Ticket aggregate:
--   id, userId (FK→users), eventId (FK→events),
--   qrCode, status, purchasedAt (ZonedDateTime → TIMESTAMPTZ)
--
-- TicketStatus enum: PURCHASED, USED, CANCELLED
-- Un usuario solo puede tener un ticket por evento (UNIQUE user+event)
-- ─────────────────────────────────────────────────────────────────────────

CREATE TABLE tickets (
    id           UUID         NOT NULL,
    user_id      UUID         NOT NULL,
    event_id     UUID         NOT NULL,
    qr_code      VARCHAR(512) NOT NULL,
    status       VARCHAR(20)  NOT NULL DEFAULT 'PURCHASED',
    purchased_at TIMESTAMPTZ  NOT NULL,

    CONSTRAINT pk_tickets         PRIMARY KEY (id),
    CONSTRAINT uq_tickets_qr      UNIQUE (qr_code),
    CONSTRAINT uq_tickets_user_event UNIQUE (user_id, event_id),
    CONSTRAINT fk_tickets_user    FOREIGN KEY (user_id)  REFERENCES users  (id) ON DELETE RESTRICT,
    CONSTRAINT fk_tickets_event   FOREIGN KEY (event_id) REFERENCES events (id) ON DELETE RESTRICT,
    CONSTRAINT chk_tickets_status CHECK (status IN ('PURCHASED', 'USED', 'CANCELLED'))
);

CREATE INDEX idx_tickets_user_id  ON tickets (user_id);
CREATE INDEX idx_tickets_event_id ON tickets (event_id);
CREATE INDEX idx_tickets_status   ON tickets (status);

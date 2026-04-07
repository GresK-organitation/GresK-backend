-- ─────────────────────────────────────────────────────────────────────────
-- Migración V6: Tabla tickets
-- ─────────────────────────────────────────────────────────────────────────

CREATE TABLE IF NOT EXISTS tickets (
    id            UUID          PRIMARY KEY,
    user_id       UUID          NOT NULL,
    -- Note: FK to users(id) will be added once the users table migration is in place
    event_id      UUID          NOT NULL REFERENCES events(id) ON DELETE RESTRICT,
    qr_code       VARCHAR(512)  NOT NULL UNIQUE,
    status        VARCHAR(20)   NOT NULL DEFAULT 'PURCHASED'
                      CHECK (status IN ('PURCHASED', 'USED', 'CANCELLED')),
    purchased_at  TIMESTAMPTZ   NOT NULL,
    CONSTRAINT uq_user_event UNIQUE (user_id, event_id)
);

CREATE INDEX IF NOT EXISTS idx_tickets_user_id  ON tickets (user_id);
CREATE INDEX IF NOT EXISTS idx_tickets_event_id ON tickets (event_id);

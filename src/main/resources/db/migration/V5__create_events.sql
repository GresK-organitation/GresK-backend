-- ─────────────────────────────────────────────────────────────────────────
-- Migración V5: Tabla events
-- ─────────────────────────────────────────────────────────────────────────

CREATE TABLE IF NOT EXISTS events (
    id                 UUID         PRIMARY KEY DEFAULT gen_random_uuid(),
    title              VARCHAR(255) NOT NULL,
    promoter_id        UUID         NOT NULL REFERENCES promoters(id) ON DELETE RESTRICT,
    status             VARCHAR(20)  NOT NULL DEFAULT 'DRAFT'
                           CHECK (status IN ('DRAFT', 'PUBLISHED', 'FINISHED', 'CANCELLED')),
    genre              VARCHAR(20)
                           CHECK (genre IN ('ELECTRONIC', 'JAZZ', 'ROCK', 'INDIE', 'HIP_HOP',
                                            'CLASSICAL', 'FLAMENCO', 'POP', 'REGGAETON', 'SURPRISE')),
    amount             NUMERIC(10, 2),
    currency           VARCHAR(10),
    total_capacity     INTEGER,
    available_capacity INTEGER,
    event_date         TIMESTAMPTZ,
    city               VARCHAR(100),
    address            VARCHAR(255),
    venue              VARCHAR(255),
    reveal_at          TIMESTAMPTZ,
    created_at         TIMESTAMPTZ  NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at         TIMESTAMPTZ  NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_events_city_event_date ON events (city, event_date);
CREATE INDEX IF NOT EXISTS idx_events_genre_status    ON events (genre, status);
CREATE INDEX IF NOT EXISTS idx_events_promoter_id     ON events (promoter_id);

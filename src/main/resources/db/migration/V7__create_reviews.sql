-- ─────────────────────────────────────────────────────────────────────────────
-- V7: reviews + event community rating stats
--
-- Review aggregate:
--   id, user_id (FK→users), event_id (FK→events), ticket_id (FK→tickets, UNIQUE),
--   artist/sound/ambience/venue/setlist/overall ratings (1-5),
--   comment (optional, max 300), photo_url (optional),
--   points_awarded, status, created_at, updated_at
--
-- Business rules:
--   - One review per ticket (UNIQUE ticket_id)
--   - Base points: 50 | comment bonus: 25 | photo bonus: 20
--   - Reviews go directly to PUBLISHED status
--
-- Event rating stats (denormalized running averages):
--   review_count, avg_overall/artist/sound/ambience/venue/setlist_rating
-- ─────────────────────────────────────────────────────────────────────────────

CREATE TABLE reviews (
    id               UUID         NOT NULL,
    user_id          UUID         NOT NULL,
    event_id         UUID         NOT NULL,
    ticket_id        UUID         NOT NULL,

    -- Ratings 1-5
    artist_rating    SMALLINT     NOT NULL,
    sound_rating     SMALLINT     NOT NULL,
    ambience_rating  SMALLINT     NOT NULL,
    venue_rating     SMALLINT     NOT NULL,
    setlist_rating   SMALLINT     NOT NULL,
    overall_rating   SMALLINT     NOT NULL,

    -- Optional content
    comment          VARCHAR(300),
    photo_url        VARCHAR(512),

    points_awarded   SMALLINT     NOT NULL DEFAULT 50,
    status           VARCHAR(20)  NOT NULL DEFAULT 'PUBLISHED',

    created_at       TIMESTAMPTZ  NOT NULL,
    updated_at       TIMESTAMPTZ  NOT NULL,

    CONSTRAINT pk_reviews           PRIMARY KEY (id),
    CONSTRAINT uq_reviews_ticket    UNIQUE (ticket_id),
    CONSTRAINT fk_reviews_user      FOREIGN KEY (user_id)   REFERENCES users   (id) ON DELETE RESTRICT,
    CONSTRAINT fk_reviews_event     FOREIGN KEY (event_id)  REFERENCES events  (id) ON DELETE RESTRICT,
    CONSTRAINT fk_reviews_ticket    FOREIGN KEY (ticket_id) REFERENCES tickets (id) ON DELETE RESTRICT,
    CONSTRAINT chk_reviews_status   CHECK (status IN ('PUBLISHED', 'HIDDEN')),
    CONSTRAINT chk_reviews_artist   CHECK (artist_rating   BETWEEN 1 AND 5),
    CONSTRAINT chk_reviews_sound    CHECK (sound_rating    BETWEEN 1 AND 5),
    CONSTRAINT chk_reviews_ambience CHECK (ambience_rating BETWEEN 1 AND 5),
    CONSTRAINT chk_reviews_venue    CHECK (venue_rating    BETWEEN 1 AND 5),
    CONSTRAINT chk_reviews_setlist  CHECK (setlist_rating  BETWEEN 1 AND 5),
    CONSTRAINT chk_reviews_overall  CHECK (overall_rating  BETWEEN 1 AND 5)
);

CREATE INDEX idx_reviews_user_id  ON reviews (user_id);
CREATE INDEX idx_reviews_event_id ON reviews (event_id);
CREATE INDEX idx_reviews_status   ON reviews (status);


-- ── Event community rating stats ──────────────────────────────────────────────

ALTER TABLE events
    ADD COLUMN review_count        INTEGER      NOT NULL DEFAULT 0,
    ADD COLUMN avg_overall_rating  NUMERIC(4,2) NOT NULL DEFAULT 0.00,
    ADD COLUMN avg_artist_rating   NUMERIC(4,2) NOT NULL DEFAULT 0.00,
    ADD COLUMN avg_sound_rating    NUMERIC(4,2) NOT NULL DEFAULT 0.00,
    ADD COLUMN avg_ambience_rating NUMERIC(4,2) NOT NULL DEFAULT 0.00,
    ADD COLUMN avg_venue_rating    NUMERIC(4,2) NOT NULL DEFAULT 0.00,
    ADD COLUMN avg_setlist_rating  NUMERIC(4,2) NOT NULL DEFAULT 0.00;

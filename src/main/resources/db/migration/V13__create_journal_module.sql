-- ─────────────────────────────────────────────────────────────────────────────
-- V13: journal_entries + journal_entry_media
--
-- Journal entry aggregate ("diario musical"):
--   Free-form experience log — no ticket or catalog event required.
--   id, user_id (FK→users),
--   artist_name (free text) and/or artist_id (FK→artists, display-only link),
--   approx_date + date_precision (EXACT_DATE|MONTH|YEAR),
--   venue_name/city (free text — no Venue catalog exists yet),
--   event_id (FK→events, display-only link),
--   notes (optional, max 1000),
--   rating_criteria (JSONB, freeform user-defined [{label, value}] pairs),
--   genre, visibility (PRIVATE|PUBLIC), source (MANUAL|BULK_IMPORT)
--
-- Business rules:
--   - Either artist_name or artist_id must be present
--   - artist_id/event_id use ON DELETE SET NULL (not RESTRICT like reviews):
--     a diary entry is a personal memory and must survive the catalog
--     artist/event being deleted elsewhere, degrading to free text.
--   - Never feeds events.avg_* / artist avg rating — those stay exclusive
--     to ticket-verified reviews.
--
-- Journal entry media (photos + short video clips, max 15s):
--   entry_id + media_type (PHOTO|VIDEO) + Cloudinary asset_id + display_order
-- ─────────────────────────────────────────────────────────────────────────────

CREATE TABLE journal_entries (
    id                 UUID          NOT NULL,
    user_id            UUID          NOT NULL,

    artist_name        VARCHAR(150),
    artist_id          UUID,

    approx_date        DATE          NOT NULL,
    date_precision     VARCHAR(10)   NOT NULL,

    venue_name         VARCHAR(150),
    city               VARCHAR(100),

    event_id           UUID,

    notes              VARCHAR(1000),

    rating_criteria    JSONB         NOT NULL DEFAULT '[]',

    genre              VARCHAR(30),

    visibility         VARCHAR(10)   NOT NULL DEFAULT 'PRIVATE',
    source             VARCHAR(20)   NOT NULL DEFAULT 'MANUAL',

    created_at         TIMESTAMPTZ   NOT NULL,
    updated_at         TIMESTAMPTZ   NOT NULL,

    CONSTRAINT pk_journal_entries         PRIMARY KEY (id),
    CONSTRAINT fk_journal_entries_user    FOREIGN KEY (user_id)   REFERENCES users   (id) ON DELETE RESTRICT,
    CONSTRAINT fk_journal_entries_artist  FOREIGN KEY (artist_id) REFERENCES artists (id) ON DELETE SET NULL,
    CONSTRAINT fk_journal_entries_event   FOREIGN KEY (event_id)  REFERENCES events  (id) ON DELETE SET NULL,
    CONSTRAINT chk_journal_precision      CHECK (date_precision IN ('EXACT_DATE', 'MONTH', 'YEAR')),
    CONSTRAINT chk_journal_visibility     CHECK (visibility IN ('PRIVATE', 'PUBLIC')),
    CONSTRAINT chk_journal_source         CHECK (source IN ('MANUAL', 'BULK_IMPORT')),
    CONSTRAINT chk_journal_artist_present CHECK (artist_name IS NOT NULL OR artist_id IS NOT NULL)
);

CREATE INDEX idx_journal_entries_user_id     ON journal_entries (user_id);
CREATE INDEX idx_journal_entries_artist_id   ON journal_entries (artist_id);
CREATE INDEX idx_journal_entries_event_id    ON journal_entries (event_id);
CREATE INDEX idx_journal_entries_approx_date ON journal_entries (approx_date);


-- ── Journal entry media (photos + video clips) ────────────────────────────────

CREATE TABLE journal_entry_media (
    id                UUID         NOT NULL,
    entry_id          UUID         NOT NULL,
    media_type        VARCHAR(10)  NOT NULL,
    asset_id          VARCHAR(255) NOT NULL,
    display_order     SMALLINT     NOT NULL DEFAULT 0,
    duration_seconds  SMALLINT,

    created_at        TIMESTAMPTZ  NOT NULL,

    CONSTRAINT pk_journal_entry_media       PRIMARY KEY (id),
    CONSTRAINT fk_journal_entry_media_entry FOREIGN KEY (entry_id) REFERENCES journal_entries (id) ON DELETE CASCADE,
    CONSTRAINT chk_journal_media_type       CHECK (media_type IN ('PHOTO', 'VIDEO')),
    CONSTRAINT chk_journal_media_duration   CHECK (media_type = 'PHOTO' OR duration_seconds IS NOT NULL),
    CONSTRAINT chk_journal_media_max_dur    CHECK (duration_seconds IS NULL OR duration_seconds <= 15)
);

CREATE INDEX idx_journal_entry_media_entry_id ON journal_entry_media (entry_id);

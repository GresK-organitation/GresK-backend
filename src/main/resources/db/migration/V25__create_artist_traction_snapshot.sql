-- ─────────────────────────────────────────────────────────────────────────
-- V25: artist_traction_snapshot + artist_traction_city_audience
--
-- Recopilado cada 3 días por BandsintownTractionScheduler (calcado del
-- patrón de artist_metrics_snapshot / ArtistMetricsSnapshotScheduler).
-- TractionSource enum: SPOTIFY_FOR_ARTISTS, BANDSINTOWN, CHARTMETRIC, MANUAL
-- ─────────────────────────────────────────────────────────────────────────

CREATE TABLE artist_traction_snapshot (
    id                          UUID        NOT NULL,
    artist_id                   UUID        NOT NULL,
    snapshot_date               DATE        NOT NULL,
    bandsintown_followers       INTEGER,
    bandsintown_upcoming_shows  INTEGER,
    created_at                  TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT pk_artist_traction_snapshot PRIMARY KEY (id),
    CONSTRAINT fk_traction_snapshot_artist FOREIGN KEY (artist_id) REFERENCES artists (id) ON DELETE CASCADE,
    CONSTRAINT uq_traction_artist_date     UNIQUE (artist_id, snapshot_date)
);

CREATE INDEX idx_traction_snapshot_date ON artist_traction_snapshot (snapshot_date);

-- ── artist_traction_city_audience (@ElementCollection) ────────────────────
CREATE TABLE artist_traction_city_audience (
    snapshot_id     UUID         NOT NULL,
    city            VARCHAR(150) NOT NULL,
    country         VARCHAR(150) NOT NULL,
    audience_score  INTEGER,
    source          VARCHAR(30)  NOT NULL,

    CONSTRAINT fk_traction_city_snapshot FOREIGN KEY (snapshot_id) REFERENCES artist_traction_snapshot (id) ON DELETE CASCADE,
    CONSTRAINT chk_traction_city_source  CHECK (source IN ('SPOTIFY_FOR_ARTISTS', 'BANDSINTOWN', 'CHARTMETRIC', 'MANUAL'))
);

CREATE INDEX idx_traction_city_snapshot_id ON artist_traction_city_audience (snapshot_id);

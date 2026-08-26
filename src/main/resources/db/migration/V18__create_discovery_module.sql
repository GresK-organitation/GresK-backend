-- ─────────────────────────────────────────────────────────────────────────
-- V18: artist_discovery_profile + artist_demand_signals
--
-- GresK Discovery: perfil de descubrimiento de artistas emergentes.
-- artist_discovery_profile es un agregado propio del módulo `discovery`,
-- 1:1 con `artists`, recalculado por completo semanalmente (no se muta un
-- campo aislado) — mismo criterio que user_music_dna (V17).
--
-- No usa PostGIS/earthdistance: los filtros geográficos son por texto
-- (mb_city / mb_country), consistente con `users.city` (texto libre).
-- ─────────────────────────────────────────────────────────────────────────

CREATE TABLE artist_discovery_profile (
    id                        UUID          NOT NULL,
    artist_id                 UUID          NOT NULL,

    size_tier                 VARCHAR(20)   NOT NULL DEFAULT 'NO_SPOTIFY',
    spotify_popularity        SMALLINT,

    musicbrainz_id            VARCHAR(50),
    mb_country                VARCHAR(100),
    mb_city                   VARCHAR(100),
    mb_begin_year             INTEGER,

    gresk_review_count        INTEGER       NOT NULL DEFAULT 0,
    gresk_demand_count        INTEGER       NOT NULL DEFAULT 0,
    gresk_verified_attendees  INTEGER       NOT NULL DEFAULT 0,
    known_by_count            INTEGER       NOT NULL DEFAULT 0,

    gresk_score                DECIMAL(5,2)  NOT NULL DEFAULT 0.0,

    has_upcoming_events        BOOLEAN       NOT NULL DEFAULT false,
    next_event_date            DATE,
    next_event_city            VARCHAR(100),

    first_discovered_at        TIMESTAMPTZ   NOT NULL,
    calculated_at               TIMESTAMPTZ   NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT pk_artist_discovery_profile PRIMARY KEY (id),
    CONSTRAINT uq_discovery_profile_artist UNIQUE (artist_id),
    CONSTRAINT fk_discovery_profile_artist FOREIGN KEY (artist_id) REFERENCES artists (id) ON DELETE CASCADE,
    CONSTRAINT chk_discovery_size_tier CHECK (size_tier IN ('NO_SPOTIFY', 'MICRO', 'SMALL', 'EMERGING', 'GROWING', 'ESTABLISHED')),
    CONSTRAINT chk_discovery_score     CHECK (gresk_score >= 0.0 AND gresk_score <= 10.0)
);

CREATE TABLE artist_demand_signals (
    id          UUID         NOT NULL,
    artist_id   UUID         NOT NULL,
    user_id     UUID         NOT NULL,
    city        VARCHAR(100) NOT NULL,
    created_at  TIMESTAMPTZ  NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT pk_artist_demand_signals PRIMARY KEY (id),
    CONSTRAINT fk_demand_signals_artist FOREIGN KEY (artist_id) REFERENCES artists (id) ON DELETE CASCADE,
    CONSTRAINT fk_demand_signals_user   FOREIGN KEY (user_id)   REFERENCES users (id)   ON DELETE CASCADE,
    CONSTRAINT uq_demand_signal_artist_user UNIQUE (artist_id, user_id)
);

CREATE INDEX idx_discovery_size_tier    ON artist_discovery_profile (size_tier);
CREATE INDEX idx_discovery_geo          ON artist_discovery_profile (mb_city, mb_country);
CREATE INDEX idx_discovery_upcoming     ON artist_discovery_profile (has_upcoming_events, next_event_date);
CREATE INDEX idx_discovery_score        ON artist_discovery_profile (gresk_score DESC);
CREATE INDEX idx_demand_signals_artist  ON artist_demand_signals (artist_id);
CREATE INDEX idx_demand_signals_created ON artist_demand_signals (created_at);

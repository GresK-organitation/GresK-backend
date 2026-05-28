-- ─────────────────────────────────────────────────────────────────────────
-- V4: artists + artist_genres + artist_tags + artist_spotify_genres
--
-- Artist aggregate:
--   id, promoter_id (FK→promoters), name, origin,
--   image_url, bio, status, fee, followers,
--   contact, instagram_url, spotify_url,
--   spotify_artist_id, spotify_name, spotify_image_url,
--   events_played, avg_rating, createdAt
--   + updatedAt / version (Hibernate infra)
--
-- ArtistStatus enum: AVAILABLE, NEGOTIATING, CONFIRMED, INACTIVE
-- Un artista pertenece a una promotora (promoter_id NOT NULL)
-- La unicidad de contacto se aplica por promotora (validada en el dominio)
-- spotify_artist_id: ID del artista en Spotify (opcional, vinculación manual)
-- ─────────────────────────────────────────────────────────────────────────

CREATE TABLE artists (
    id                  UUID             NOT NULL,
    promoter_id         UUID             NOT NULL,
    name                VARCHAR(100)     NOT NULL,
    origin              VARCHAR(150)     NOT NULL,
    image_asset_id      VARCHAR(2048),
    bio                 VARCHAR(600),
    status              VARCHAR(20)      NOT NULL DEFAULT 'AVAILABLE',
    fee                 VARCHAR(100),
    followers           VARCHAR(50),
    contact             VARCHAR(255)     NOT NULL,
    instagram_url       VARCHAR(500),
    spotify_url         VARCHAR(500),
    spotify_artist_id   VARCHAR(50)      UNIQUE,
    spotify_name        VARCHAR(200),
    spotify_image_url   VARCHAR(500),
    events_played       INTEGER          NOT NULL DEFAULT 0,
    avg_rating          DOUBLE PRECISION NOT NULL DEFAULT 0.0,
    created_at          TIMESTAMPTZ      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at          TIMESTAMPTZ      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    version             BIGINT           NOT NULL DEFAULT 0,

    CONSTRAINT pk_artists           PRIMARY KEY (id),
    CONSTRAINT fk_artists_promoter  FOREIGN KEY (promoter_id) REFERENCES promoters (id) ON DELETE CASCADE,
    CONSTRAINT chk_artists_status   CHECK (status IN ('AVAILABLE', 'NEGOTIATING', 'CONFIRMED', 'INACTIVE')),
    CONSTRAINT chk_artists_rating   CHECK (avg_rating >= 0.0 AND avg_rating <= 5.0),
    CONSTRAINT chk_artists_events   CHECK (events_played >= 0)
);

CREATE TABLE artist_genres (
    artist_id UUID        NOT NULL,
    genre     VARCHAR(50) NOT NULL,

    CONSTRAINT pk_artist_genres         PRIMARY KEY (artist_id, genre),
    CONSTRAINT fk_artist_genres_artist  FOREIGN KEY (artist_id) REFERENCES artists (id) ON DELETE CASCADE
);

CREATE TABLE artist_tags (
    artist_id UUID         NOT NULL,
    tag       VARCHAR(100) NOT NULL,

    CONSTRAINT pk_artist_tags         PRIMARY KEY (artist_id, tag),
    CONSTRAINT fk_artist_tags_artist  FOREIGN KEY (artist_id) REFERENCES artists (id) ON DELETE CASCADE
);

CREATE TABLE artist_spotify_genres (
    artist_id UUID         NOT NULL,
    genre     VARCHAR(100) NOT NULL,

    CONSTRAINT pk_artist_spotify_genres        PRIMARY KEY (artist_id, genre),
    CONSTRAINT fk_artist_spotify_genres_artist FOREIGN KEY (artist_id) REFERENCES artists (id) ON DELETE CASCADE
);

CREATE INDEX idx_artists_promoter_id       ON artists (promoter_id);
CREATE INDEX idx_artists_status            ON artists (status);
CREATE INDEX idx_artist_genres_genre       ON artist_genres (genre);
CREATE INDEX idx_artists_spotify_artist_id ON artists (spotify_artist_id);

-- ── Historial de métricas de Spotify por artista ──────────────────────────
-- Recopiladas cada 3 días por ArtistMetricsSnapshotScheduler.
-- Se retienen 365 días; registros más antiguos se purgan el 1º de cada mes.

CREATE TABLE artist_metrics_snapshot (
    id                  UUID             NOT NULL,
    artist_id           UUID             NOT NULL,
    snapshot_date       DATE             NOT NULL,
    spotify_popularity  SMALLINT,
    spotify_followers   INTEGER,
    last_release_date   DATE,
    total_releases      INTEGER,
    created_at          TIMESTAMPTZ      NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT pk_artist_metrics_snapshot        PRIMARY KEY (id),
    CONSTRAINT fk_metrics_snapshot_artist        FOREIGN KEY (artist_id) REFERENCES artists (id) ON DELETE CASCADE,
    CONSTRAINT uq_metrics_artist_date            UNIQUE (artist_id, snapshot_date)
);

CREATE INDEX idx_metrics_snapshot_date ON artist_metrics_snapshot (snapshot_date);

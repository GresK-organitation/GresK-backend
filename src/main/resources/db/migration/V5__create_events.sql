-- ─────────────────────────────────────────────────────────────────────────
-- V5: events
--
-- Event aggregate:
--   id, title, promoter_id (FK→promoters),
--   status, genre (MusicGenre),
--   price (amount + currency), discountedPrice (discounted_amount),
--   capacity (total_capacity + available_capacity),
--   eventDate, revealAt, createdAt,
--   location (street + city + country + venue + latitude + longitude),
--   coverImage (cover_image_asset_id),
--   artist_id (FK→artists, nullable — Artist is its own aggregate)
--   + updatedAt (Hibernate infra)
--
-- EventStatus enum: DRAFT, PUBLISHED, FINISHED, CANCELLED
-- MusicGenre enum: ROCK, POP, TECHNO, REGGAETON, HIP_HOP, HOUSE, INDIE,
--                  METAL, TRAP, JAZZ, CLASSICAL, FLAMENCO, R_AND_B,
--                  PUNK, LATIN_JAZZ, ELECTRONIC, SURPRISE
-- ─────────────────────────────────────────────────────────────────────────

CREATE TABLE events (
    id                  UUID          NOT NULL,
    title               VARCHAR(255)  NOT NULL,
    promoter_id         UUID          NOT NULL,

    -- ciclo de vida
    status              VARCHAR(20)   NOT NULL DEFAULT 'DRAFT',

    -- género musical
    genre               VARCHAR(20),

    -- precio original
    amount              NUMERIC(10,2),
    currency            VARCHAR(10),

    -- precio con descuento (null = sin descuento)
    discounted_amount   NUMERIC(10,2),

    -- aforo
    total_capacity      INTEGER,
    available_capacity  INTEGER,

    -- fechas
    event_date          TIMESTAMPTZ,
    reveal_at           TIMESTAMPTZ,
    created_at          TIMESTAMPTZ   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at          TIMESTAMPTZ   NOT NULL DEFAULT CURRENT_TIMESTAMP,

    -- ubicación (Location → Address + Coordinates + venue)
    street              VARCHAR(255),
    city                VARCHAR(100),
    country             VARCHAR(100),
    venue               VARCHAR(255),
    latitude            DOUBLE PRECISION,
    longitude           DOUBLE PRECISION,

    -- imagen de portada (Cloudinary asset_id)
    cover_image_asset_id VARCHAR(512),

    -- artista (referencia FK al agregado Artist; null si no hay artista asignado)
    artist_id           UUID,

    CONSTRAINT pk_events              PRIMARY KEY (id),
    CONSTRAINT fk_events_promoter     FOREIGN KEY (promoter_id) REFERENCES promoters (id) ON DELETE RESTRICT,
    CONSTRAINT fk_events_artist       FOREIGN KEY (artist_id)   REFERENCES artists  (id) ON DELETE SET NULL,
    CONSTRAINT chk_events_status      CHECK (status IN ('DRAFT', 'PUBLISHED', 'FINISHED', 'CANCELLED')),
    CONSTRAINT chk_events_genre       CHECK (genre IN (
        'ROCK', 'POP', 'TECHNO', 'REGGAETON', 'HIP_HOP', 'HOUSE',
        'INDIE', 'METAL', 'TRAP', 'JAZZ', 'CLASSICAL', 'FLAMENCO',
        'R_AND_B', 'PUNK', 'LATIN_JAZZ', 'ELECTRONIC', 'SURPRISE'
    ))
);

CREATE INDEX idx_events_promoter_id  ON events (promoter_id);
CREATE INDEX idx_events_status       ON events (status);
CREATE INDEX idx_events_genre_status ON events (genre, status);
CREATE INDEX idx_events_city_date    ON events (city, event_date);
CREATE INDEX idx_events_artist_id    ON events (artist_id);

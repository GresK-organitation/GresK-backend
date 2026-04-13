-- ─────────────────────────────────────────────────────────────────────────
-- V15: Refactor dominio Event
--   · Amplía el CHECK de genre para incluir todos los valores de MusicGenre
--   · Convierte event_date / reveal_at a TIMESTAMPTZ (ya lo son, no-op)
--   · Añade columnas: street, country, latitude, longitude,
--                     discounted_amount, cover_image_url,
--                     artist_name, artist_image_url
--   · Renombra 'address' → 'street' (el campo ya existe como 'address',
--     se añade 'street' y se deja 'address' nullable para compatibilidad)
-- ─────────────────────────────────────────────────────────────────────────

-- 1. Ampliar CHECK de genre (drop + recreate)
ALTER TABLE events DROP CONSTRAINT IF EXISTS events_genre_check;
ALTER TABLE events
    ADD CONSTRAINT events_genre_check
    CHECK (genre IN (
        'ROCK','POP','TECHNO','REGGAETON','HIP_HOP','HOUSE',
        'INDIE','METAL','TRAP','JAZZ','CLASSICAL','FLAMENCO',
        'R_AND_B','PUNK','LATIN_JAZZ','ELECTRONIC','SURPRISE'
    ));

-- 2. Nuevas columnas de ubicación
ALTER TABLE events
    ADD COLUMN IF NOT EXISTS street   VARCHAR(255),
    ADD COLUMN IF NOT EXISTS country  VARCHAR(100),
    ADD COLUMN IF NOT EXISTS latitude  DOUBLE PRECISION,
    ADD COLUMN IF NOT EXISTS longitude DOUBLE PRECISION;

-- Migrar datos existentes: address → street
UPDATE events SET street = address WHERE street IS NULL AND address IS NOT NULL;

-- 3. Precio con descuento
ALTER TABLE events
    ADD COLUMN IF NOT EXISTS discounted_amount NUMERIC(10, 2);

-- 4. Imagen de portada y artista
ALTER TABLE events
    ADD COLUMN IF NOT EXISTS cover_image_url  VARCHAR(512),
    ADD COLUMN IF NOT EXISTS artist_name      VARCHAR(255),
    ADD COLUMN IF NOT EXISTS artist_image_url VARCHAR(512);

-- 5. Índices adicionales
CREATE INDEX IF NOT EXISTS idx_events_artist_name ON events (artist_name);
CREATE INDEX IF NOT EXISTS idx_events_location    ON events (city, country, event_date);

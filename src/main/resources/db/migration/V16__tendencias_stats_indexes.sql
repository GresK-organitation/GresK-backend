-- Índices de soporte para las queries de agregación de Tendencias (sub-hexágono
-- stats). No crea tablas: las métricas se calculan en vivo sobre reviews/events/
-- tickets ya existentes.

CREATE INDEX IF NOT EXISTS idx_reviews_created_at ON reviews (created_at);
CREATE INDEX IF NOT EXISTS idx_events_genre_event_date ON events (genre, event_date);
CREATE INDEX IF NOT EXISTS idx_events_venue_normalized ON events (LOWER(TRIM(venue)));

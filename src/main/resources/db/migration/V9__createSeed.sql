-- ─────────────────────────────────────────────────────────────────────────────
-- 1. CUENTAS PARA LAS PROMOTORAS
-- ─────────────────────────────────────────────────────────────────────────────
INSERT INTO accounts (id, email, password_hash, status)
VALUES
    ('11111111-1111-1111-1111-111111111101', 'contacto@barcelonalive.demo', '$2a$10$abcdefghijklmnopqrstuvwxyz123456', 'ACTIVE'),
    ('11111111-1111-1111-1111-111111111102', 'booking@soundfest.demo', '$2a$10$abcdefghijklmnopqrstuvwxyz123456', 'ACTIVE');

INSERT INTO account_roles (account_id, role)
VALUES
    ('11111111-1111-1111-1111-111111111101', 'PROMOTER'),
    ('11111111-1111-1111-1111-111111111102', 'PROMOTER');

-- ─────────────────────────────────────────────────────────────────────────────
-- 2. PROMOTORAS
-- ─────────────────────────────────────────────────────────────────────────────
INSERT INTO promoters (
    id, account_id, email, name, description,
    logo_asset_id, street, city, country, phone, website
)
VALUES
    (
        '11111111-1111-1111-1111-111111111101',
        '11111111-1111-1111-1111-111111111101',
        'contacto@barcelonalive.demo',
        'Barcelona Live Events',
        'Especialistas en festivales de música electrónica, techno y clubbing.',
        'promoters/logos/promoter1',
        'Carrer de Mallorca 123', 'Barcelona', 'España',
        '+34931234567', 'https://barcelonalive.demo'
    ),
    (
        '11111111-1111-1111-1111-111111111102',
        '11111111-1111-1111-1111-111111111102',
        'booking@soundfest.demo',
        'SoundFest Productions',
        'Promotora enfocada en conciertos indie, rock y pop acústico.',
        'promoters/logos/promoter2',
        'Av. Diagonal 456', 'Barcelona', 'España',
        '+34939876543', 'https://soundfest.demo'
    );

INSERT INTO promoter_genres (promoter_id, genre)
VALUES
    ('11111111-1111-1111-1111-111111111101', 'TECHNO'),
    ('11111111-1111-1111-1111-111111111101', 'ELECTRONIC'),
    ('11111111-1111-1111-1111-111111111102', 'INDIE'),
    ('11111111-1111-1111-1111-111111111102', 'POP');

-- ─────────────────────────────────────────────────────────────────────────────
-- 3. ARTISTAS BASE
-- ─────────────────────────────────────────────────────────────────────────────
INSERT INTO artists (
    id, promoter_id, name, origin, bio, status, fee, followers, contact, events_played, avg_rating
)
VALUES
    (
        '33333333-3333-3333-3333-333333333301',
        '11111111-1111-1111-1111-111111111101',
        'Synth Wave Project', 'Barcelona, España',
        'Dúo de electrónica y síntesis analógica en directo.',
        'CONFIRMED', '1500 EUR', '12500', 'booking@synthwave.demo', 12, 4.85
    ),
    (
        '33333333-3333-3333-3333-333333333302',
        '11111111-1111-1111-1111-111111111102',
        'Indie Echoes', 'Madrid, España',
        'Banda de indie rock alternativo con potentes directos.',
        'CONFIRMED', '2000 EUR', '28000', 'contact@indieechoes.demo', 8, 4.70
    );

-- ─────────────────────────────────────────────────────────────────────────────
-- 4. CONCIERTOS / EVENTOS (FECHAS RECIENTES Y RECIÉN PUBLICADAS)
-- ─────────────────────────────────────────────────────────────────────────────
INSERT INTO events (
    id, title, promoter_id, artist_id, status, genre,
    amount, currency, total_capacity, available_capacity,
    event_date, cover_image_asset_id,
    street, city, country, venue, latitude, longitude
)
VALUES
-- Evento 1: HOY MISMO (En 4 horas)
(
    '44444444-4444-4444-4444-444444444401',
    'Synthwave Night Vol. 1',
    '11111111-1111-1111-1111-111111111101',
    '33333333-3333-3333-3333-333333333301',
    'PUBLISHED', 'ELECTRONIC',
    25.00, 'EUR', 300, 45,
    CURRENT_TIMESTAMP + INTERVAL '4 hours', -- <--- HOY
    'events/covers/concert1',
    'Carrer de Tànger 98', 'Barcelona', 'España', 'Sala Razzmatazz 2', 41.3977, 2.1911
),
-- Evento 2: MAÑANA (A las 21:00 aprox)
(
    '44444444-4444-4444-4444-444444444402',
    'Indie Rock Live Showcase',
    '11111111-1111-1111-1111-111111111102',
    '33333333-3333-3333-3333-333333333302',
    'PUBLISHED', 'INDIE',
    18.50, 'EUR', 200, 12,
    CURRENT_TIMESTAMP + INTERVAL '1 day', -- <--- MAÑANA
    'events/covers/concert2',
    'Carrer de Nou de la Rambla 113', 'Barcelona', 'España', 'Sala Apolo', 41.3745, 2.1689
),
-- Evento 3: PASADO MAÑANA (En 2 días)
(
    '44444444-4444-4444-4444-444444444403',
    'Techno Underground Session',
    '11111111-1111-1111-1111-111111111101',
    '33333333-3333-3333-3333-333333333301',
    'PUBLISHED', 'TECHNO',
    30.00, 'EUR', 500, 210,
    CURRENT_TIMESTAMP + INTERVAL '2 days', -- <--- EN 2 DÍAS
    'events/covers/concert3',
    'Plaça de Espanya s/n', 'Barcelona', 'España', 'La Terrazza', 41.3688, 2.1481
),
-- Evento 4: ESTE FIN DE SEMANA (En 3 días)
(
    '44444444-4444-4444-4444-444444444404',
    'Acoustic Pop & Soul Night',
    '11111111-1111-1111-1111-111111111102',
    '33333333-3333-3333-3333-333333333302',
    'PUBLISHED', 'POP',
    15.00, 'EUR', 120, 8,
    CURRENT_TIMESTAMP + INTERVAL '3 days', -- <--- EN 3 DÍAS
    'events/covers/concert4',
    'Carrer de Mallorca 214', 'Barcelona', 'España', 'Luz de Gas', 41.3922, 2.1558
),
-- Evento 5: PRÓXIMA SEMANA (En 5 días)
(
    '44444444-4444-4444-4444-444444444405',
    'Festival Closing Party: Summer Beats',
    '11111111-1111-1111-1111-111111111101',
    '33333333-3333-3333-3333-333333333301',
    'PUBLISHED', 'ELECTRONIC',
    40.00, 'EUR', 1000, 600,
    CURRENT_TIMESTAMP + INTERVAL '5 days', -- <--- EN 5 DÍAS
    'events/covers/concert5',
    'Av. del Litoral 40', 'Barcelona', 'España', 'Pachá Barcelona', 41.3856, 2.1970
);
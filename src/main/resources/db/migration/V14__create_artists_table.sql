CREATE TABLE IF NOT EXISTS artists (
    id               UUID         PRIMARY KEY DEFAULT gen_random_uuid(),
    promoter_id      UUID         NOT NULL REFERENCES promoters(id) ON DELETE CASCADE,
    name             VARCHAR(60)  NOT NULL,
    origin           VARCHAR(100),
    image_url        VARCHAR(500),
    bio              VARCHAR(600) NOT NULL,
    status           VARCHAR(20)  NOT NULL DEFAULT 'AVAILABLE',
    fee              VARCHAR(100),
    followers        VARCHAR(50),
    contact          VARCHAR(255) NOT NULL,
    social_spotify   VARCHAR(500),
    social_instagram VARCHAR(500),
    events_played    INT          NOT NULL DEFAULT 0,
    created_at       TIMESTAMPTZ  NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at       TIMESTAMPTZ  NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS artist_genres (
    artist_id UUID         NOT NULL REFERENCES artists(id) ON DELETE CASCADE,
    genre     VARCHAR(50)  NOT NULL,
    PRIMARY KEY (artist_id, genre)
);

CREATE TABLE IF NOT EXISTS artist_tags (
    artist_id UUID          NOT NULL REFERENCES artists(id) ON DELETE CASCADE,
    tag       VARCHAR(100)  NOT NULL,
    PRIMARY KEY (artist_id, tag)
);

CREATE INDEX IF NOT EXISTS idx_artists_promoter_id ON artists(promoter_id);
CREATE INDEX IF NOT EXISTS idx_artists_status      ON artists(status);

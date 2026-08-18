-- Módulo Tendencias / sub-hexágono chronicle: agregador de crónicas editoriales.
-- Moderación a nivel de fuente (feed_sources), no de artículo: un ADMIN aprueba
-- el medio una única vez y, a partir de ahí, todo lo que llega de una fuente
-- APPROVED se publica automáticamente (ver chronicles.status).

CREATE TABLE feed_sources (
    id              UUID          NOT NULL,
    name            VARCHAR(150)  NOT NULL,
    feed_url        VARCHAR(2048) NOT NULL,
    source_url      VARCHAR(2048),
    status          VARCHAR(20)   NOT NULL DEFAULT 'PENDING_APPROVAL',
    requested_at    TIMESTAMPTZ   NOT NULL DEFAULT now(),
    reviewed_by     UUID,
    reviewed_at     TIMESTAMPTZ,
    last_fetched_at TIMESTAMPTZ,

    CONSTRAINT pk_feed_sources PRIMARY KEY (id),
    CONSTRAINT uq_feed_sources_feed_url UNIQUE (feed_url),
    CONSTRAINT chk_feed_sources_status CHECK (status IN ('PENDING_APPROVAL', 'APPROVED', 'REJECTED'))
);

CREATE INDEX idx_feed_sources_status ON feed_sources (status);

CREATE TABLE chronicles (
    id                    UUID          NOT NULL,
    feed_source_id        UUID          NOT NULL,
    title                 VARCHAR(500)  NOT NULL,
    excerpt               VARCHAR(250)  NOT NULL,
    link                  VARCHAR(2048) NOT NULL,
    guid                  VARCHAR(500)  NOT NULL,
    source_name           VARCHAR(150)  NOT NULL,
    source_url            VARCHAR(2048),
    original_published_at TIMESTAMPTZ,
    ingested_at           TIMESTAMPTZ   NOT NULL DEFAULT now(),
    status                VARCHAR(20)   NOT NULL DEFAULT 'PUBLISHED',

    CONSTRAINT pk_chronicles PRIMARY KEY (id),
    CONSTRAINT fk_chronicles_feed_source FOREIGN KEY (feed_source_id) REFERENCES feed_sources (id) ON DELETE CASCADE,
    CONSTRAINT uq_chronicles_link UNIQUE (link),
    CONSTRAINT chk_chronicles_status CHECK (status IN ('PUBLISHED', 'HIDDEN'))
);

CREATE INDEX idx_chronicles_status_published_at ON chronicles (status, original_published_at DESC);
CREATE UNIQUE INDEX uq_chronicles_feed_source_guid ON chronicles (feed_source_id, guid);

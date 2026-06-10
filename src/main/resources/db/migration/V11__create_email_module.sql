-- ─────────────────────────────────────────────────────────────────────────
-- V11: email module (Email Intelligence Engine)
--
-- email_messages        → correo entrante sincronizado desde Gmail (aggregate root)
-- email_entities        → entidades extraídas por IA de un correo
-- email_rider_versions  → versiones de rider detectadas por email para un evento
-- email_draft_replies   → borradores de respuesta generados por IA
-- promoter_gmail_tokens → credenciales OAuth de Gmail por promotora (1:1 con users)
--
-- ProcessingStatus:    PENDING, PROCESSING, DONE, FAILED, DEAD_LETTER
-- EmailClassification: RIDER, CACHE, HORARIO, CONTRATO, LOGISTICA,
--                      CONFIRMACION, CAMBIO, OTRO
-- ExtractedEntityType: DATE, AMOUNT, RIDER_ITEM, ARTIST_NAME, VENUE_NAME,
--                      CONTACT_NAME, SCHEDULE_ITEM, CONDITION, ACCOMMODATION,
--                      TRANSPORT, CHANGE_DETECTED
-- RiderVersionSource:  AI, MANUAL
--
-- NOTA: access_token / refresh_token se persisten en claro en esta fase.
--       El cifrado AES-256-GCM se introduce en el Issue #2 del EIE.
-- ─────────────────────────────────────────────────────────────────────────

-- ── email_messages ────────────────────────────────────────────────────────
CREATE TABLE email_messages (
    id                        UUID          NOT NULL,
    promoter_id               UUID          NOT NULL,
    event_id                  UUID,

    -- Identificadores del proveedor (Gmail)
    message_id_external       VARCHAR(500)  NOT NULL,
    thread_id_external        VARCHAR(500),

    -- Cabeceras y contenido
    from_address              VARCHAR(300)  NOT NULL,
    from_name                 VARCHAR(200),
    to_addresses              JSONB,
    subject                   VARCHAR(500),
    body_text                 TEXT,
    body_html                 TEXT,
    raw_headers               JSONB,

    -- Clasificación por IA
    classification            VARCHAR(50),
    classification_confidence NUMERIC(4,2),

    -- Pipeline de procesamiento
    processing_status         VARCHAR(20)   NOT NULL DEFAULT 'PENDING',
    processing_attempts       INTEGER       NOT NULL DEFAULT 0,
    last_attempt_at           TIMESTAMPTZ,
    processed_at              TIMESTAMPTZ,

    received_at               TIMESTAMPTZ   NOT NULL,
    created_at                TIMESTAMPTZ   NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT pk_email_messages       PRIMARY KEY (id),
    CONSTRAINT uq_email_external_id    UNIQUE (message_id_external),
    CONSTRAINT fk_email_promoter       FOREIGN KEY (promoter_id) REFERENCES users(id),
    CONSTRAINT fk_email_event          FOREIGN KEY (event_id)    REFERENCES events(id),
    CONSTRAINT chk_email_status        CHECK (processing_status IN
                                              ('PENDING', 'PROCESSING', 'DONE', 'FAILED', 'DEAD_LETTER')),
    CONSTRAINT chk_email_classification CHECK (classification IN
                                              ('RIDER', 'CACHE', 'HORARIO', 'CONTRATO', 'LOGISTICA',
                                               'CONFIRMACION', 'CAMBIO', 'OTRO')
                                              OR classification IS NULL)
);

-- Bandeja de entrada de la promotora (orden cronológico inverso)
CREATE INDEX idx_email_promoter ON email_messages (promoter_id, received_at DESC);

-- Correos vinculados a un evento
CREATE INDEX idx_email_event ON email_messages (event_id, received_at DESC);

-- Agrupación por hilo de conversación
CREATE INDEX idx_email_thread ON email_messages (thread_id_external);

-- Índice parcial para el worker de procesamiento (solo correos no finalizados)
CREATE INDEX idx_email_status ON email_messages (processing_status)
    WHERE processing_status NOT IN ('DONE', 'DEAD_LETTER');

-- ── email_entities ────────────────────────────────────────────────────────
CREATE TABLE email_entities (
    id                      UUID          NOT NULL,
    email_id                UUID          NOT NULL,

    entity_type             VARCHAR(50)   NOT NULL,
    entity_key              VARCHAR(200),
    entity_value            TEXT          NOT NULL,
    entity_value_normalized JSONB,
    confidence              NUMERIC(4,2),
    source_snippet          TEXT,

    requires_action         BOOLEAN       NOT NULL DEFAULT false,
    actioned_at             TIMESTAMPTZ,

    created_at              TIMESTAMPTZ   NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT pk_email_entities  PRIMARY KEY (id),
    CONSTRAINT fk_entities_email  FOREIGN KEY (email_id) REFERENCES email_messages(id) ON DELETE CASCADE,
    CONSTRAINT chk_entity_type    CHECK (entity_type IN
                                         ('DATE', 'AMOUNT', 'RIDER_ITEM', 'ARTIST_NAME', 'VENUE_NAME',
                                          'CONTACT_NAME', 'SCHEDULE_ITEM', 'CONDITION', 'ACCOMMODATION',
                                          'TRANSPORT', 'CHANGE_DETECTED'))
);

CREATE INDEX idx_entities_email ON email_entities (email_id);

-- Índice parcial para el inbox de acciones pendientes
CREATE INDEX idx_entities_action ON email_entities (requires_action)
    WHERE requires_action = true;

-- ── email_rider_versions ──────────────────────────────────────────────────
CREATE TABLE email_rider_versions (
    id              UUID          NOT NULL,
    event_id        UUID          NOT NULL,
    version_number  INTEGER       NOT NULL,
    source_email_id UUID,

    rider_data      JSONB         NOT NULL,
    diff_from_prev  JSONB,
    created_by      VARCHAR(20)   NOT NULL DEFAULT 'AI',
    notes           TEXT,

    created_at      TIMESTAMPTZ   NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT pk_email_rider_versions  PRIMARY KEY (id),
    CONSTRAINT uq_rider_event_version   UNIQUE (event_id, version_number),
    CONSTRAINT fk_rider_version_event   FOREIGN KEY (event_id)        REFERENCES events(id),
    CONSTRAINT fk_rider_version_email   FOREIGN KEY (source_email_id) REFERENCES email_messages(id),
    CONSTRAINT chk_rider_created_by     CHECK (created_by IN ('AI', 'MANUAL'))
);

-- Historial de versiones de un evento (la más reciente primero)
CREATE INDEX idx_rider_event ON email_rider_versions (event_id, version_number DESC);

-- ── email_draft_replies ───────────────────────────────────────────────────
CREATE TABLE email_draft_replies (
    id           UUID          NOT NULL,
    email_id     UUID          NOT NULL,
    promoter_id  UUID          NOT NULL,

    draft_type   VARCHAR(50)   NOT NULL,
    subject      VARCHAR(500),
    body         TEXT          NOT NULL,
    edited_body  TEXT,

    status       VARCHAR(30)   NOT NULL DEFAULT 'PENDING_REVIEW',
    approved_at  TIMESTAMPTZ,
    sent_at      TIMESTAMPTZ,

    created_at   TIMESTAMPTZ   NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT pk_email_draft_replies PRIMARY KEY (id),
    CONSTRAINT fk_drafts_email        FOREIGN KEY (email_id)    REFERENCES email_messages(id),
    CONSTRAINT fk_drafts_promoter     FOREIGN KEY (promoter_id) REFERENCES users(id)
);

-- Cola de revisión de la promotora
CREATE INDEX idx_drafts_promoter ON email_draft_replies (promoter_id, status);

-- Borradores asociados a un correo
CREATE INDEX idx_drafts_email ON email_draft_replies (email_id);

-- ── promoter_gmail_tokens ─────────────────────────────────────────────────
CREATE TABLE promoter_gmail_tokens (
    promoter_id   UUID          NOT NULL,
    access_token  TEXT          NOT NULL,
    refresh_token TEXT          NOT NULL,
    token_expiry  TIMESTAMPTZ,
    gmail_address VARCHAR(300),
    watch_expiry  TIMESTAMPTZ,
    connected_at  TIMESTAMPTZ   NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT pk_promoter_gmail_tokens PRIMARY KEY (promoter_id),
    CONSTRAINT fk_gmail_tokens_promoter FOREIGN KEY (promoter_id) REFERENCES users(id) ON DELETE CASCADE
);

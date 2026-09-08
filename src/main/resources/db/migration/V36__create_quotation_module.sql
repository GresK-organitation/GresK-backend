-- ─────────────────────────────────────────────────────────────────────────
-- V36: quotation module — automatic BOM / cost estimate per event
--
-- EventQuote → cotización de producción de un evento (aggregate root)
-- QuoteLine  → línea generada a partir de un RiderLineItem (técnico u
--              hospitality), con fulfillment y coste/proveedor asignado
-- ─────────────────────────────────────────────────────────────────────────

CREATE TABLE event_quotes (
    id           UUID        NOT NULL,
    event_id     UUID        NOT NULL UNIQUE,
    promoter_id  UUID        NOT NULL,
    currency     VARCHAR(3)  NOT NULL,
    status       VARCHAR(20) NOT NULL DEFAULT 'DRAFT',
    created_at   TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at   TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT pk_event_quotes          PRIMARY KEY (id),
    CONSTRAINT fk_event_quotes_event    FOREIGN KEY (event_id)    REFERENCES events    (id) ON DELETE CASCADE,
    CONSTRAINT fk_event_quotes_promoter FOREIGN KEY (promoter_id) REFERENCES promoters (id) ON DELETE CASCADE,
    CONSTRAINT chk_event_quotes_status  CHECK (status IN ('DRAFT', 'CONFIRMED'))
);

CREATE INDEX idx_event_quotes_promoter_id ON event_quotes (promoter_id);

CREATE TABLE quote_lines (
    id                   UUID          NOT NULL,
    quote_id             UUID          NOT NULL,
    rider_type           VARCHAR(20)   NOT NULL,
    rider_id             UUID          NOT NULL,
    line_item_id         UUID          NOT NULL,
    category             VARCHAR(20)   NOT NULL,
    description          VARCHAR(255)  NOT NULL,
    quantity             INTEGER       NOT NULL DEFAULT 1,
    fulfillment_source   VARCHAR(20)   NOT NULL DEFAULT 'UNRESOLVED',
    supplier_id          UUID,
    catalog_item_id      UUID,
    unit_cost_amount     NUMERIC(12,2),
    unit_cost_currency   VARCHAR(3),

    CONSTRAINT pk_quote_lines            PRIMARY KEY (id),
    CONSTRAINT fk_quote_lines_quote      FOREIGN KEY (quote_id)    REFERENCES event_quotes (id) ON DELETE CASCADE,
    CONSTRAINT fk_quote_lines_supplier   FOREIGN KEY (supplier_id) REFERENCES suppliers    (id) ON DELETE SET NULL,
    CONSTRAINT chk_quote_lines_rider_type CHECK (rider_type IN ('TECHNICAL', 'HOSPITALITY')),
    CONSTRAINT chk_quote_lines_category CHECK (category IN (
        'SOUND_PA','MICROPHONE','BACKLINE','LIGHTING','STAGE',
        'CATERING','DRESSING_ROOM','DIET','ACCOMMODATION','TRANSPORT','OTHER'
    )),
    CONSTRAINT chk_quote_lines_fulfillment CHECK (fulfillment_source IN (
        'UNRESOLVED','VENUE_STOCK','RENTAL_EXTERNAL','ARTIST_PROVIDED'
    ))
);

CREATE INDEX idx_quote_lines_quote_id ON quote_lines (quote_id);
CREATE INDEX idx_quote_lines_supplier_id ON quote_lines (supplier_id);

-- ─────────────────────────────────────────────────────────────────────────
-- V38: shows (módulo operativo de producción de eventos) + bitácora
--
-- Show aggregate:
--   id, promoter_id, name, scheduled_date,
--   venue booking snapshot (venue_id/name/capacity_config_*),
--   hold window (hold_*), P&L borrador (cost_line_items JSONB + supuestos de ingreso),
--   status (ShowStatus), settlement (liquidación real), linked_marketplace_event_id,
--   cancellation_reason, created_at, updated_at
--
-- show_log_entries: bitácora append-only, 1:N con shows (no FK de dominio,
-- se consulta por su propio repositorio — ver Show.java Javadoc).
--
-- ShowStatus enum: BORRADOR, OPCION_HOLD, CONFIRMADO, EN_VENTA, EN_EJECUCION,
--                  FINALIZADO, LIQUIDADO, CANCELADO
-- ─────────────────────────────────────────────────────────────────────────

CREATE TABLE shows (
    id                  UUID          NOT NULL,
    promoter_id         UUID          NOT NULL,
    name                VARCHAR(255)  NOT NULL,
    scheduled_date      TIMESTAMPTZ,

    status              VARCHAR(20)   NOT NULL DEFAULT 'BORRADOR',

    -- snapshot del recinto+aforo elegido (bloque nullable)
    venue_id                UUID,
    venue_name              VARCHAR(255),
    capacity_config_code    VARCHAR(50),
    capacity_config_label   VARCHAR(255),
    confirmed_capacity      INTEGER,

    -- ventana de hold (solo relevante en OPCION_HOLD)
    hold_placed_at          TIMESTAMPTZ,
    hold_expires_at         TIMESTAMPTZ,

    -- P&L borrador: [{category,label,amount,nature}]
    cost_line_items         JSONB,
    avg_ticket_price        NUMERIC(10,2),
    expected_sellout_percent INTEGER,
    simulation_currency     VARCHAR(10),

    -- liquidación real (bloque nullable, solo tras LIQUIDADO)
    settlement_actual_attendance INTEGER,
    settlement_actual_revenue    NUMERIC(12,2),
    settlement_actual_costs      NUMERIC(12,2),
    settlement_net_result        NUMERIC(12,2),
    settlement_settled_at        TIMESTAMPTZ,

    linked_marketplace_event_id  UUID,
    cancellation_reason          TEXT,

    created_at              TIMESTAMPTZ   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at              TIMESTAMPTZ   NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT pk_shows            PRIMARY KEY (id),
    CONSTRAINT fk_shows_promoter   FOREIGN KEY (promoter_id) REFERENCES promoters (id) ON DELETE RESTRICT,
    CONSTRAINT chk_shows_status    CHECK (status IN (
        'BORRADOR', 'OPCION_HOLD', 'CONFIRMADO', 'EN_VENTA',
        'EN_EJECUCION', 'FINALIZADO', 'LIQUIDADO', 'CANCELADO'
    ))
);

CREATE INDEX idx_shows_promoter_id ON shows (promoter_id);
CREATE INDEX idx_shows_status      ON shows (status);

-- Índice parcial: candidatos del scheduler de expiración de holds.
CREATE INDEX idx_shows_expirable_holds
    ON shows (hold_expires_at)
   WHERE status = 'OPCION_HOLD';

CREATE TABLE show_log_entries (
    id              UUID          NOT NULL,
    show_id         UUID          NOT NULL,
    type            VARCHAR(20)   NOT NULL,
    actor           VARCHAR(255),
    occurred_at     TIMESTAMPTZ   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    description     TEXT          NOT NULL,
    related_party   VARCHAR(255),
    attachments     JSONB         NOT NULL DEFAULT '[]'::jsonb,

    CONSTRAINT pk_show_log_entries      PRIMARY KEY (id),
    CONSTRAINT fk_show_log_entries_show FOREIGN KEY (show_id) REFERENCES shows (id) ON DELETE CASCADE
);

CREATE INDEX idx_show_log_entries_show_id ON show_log_entries (show_id, occurred_at);

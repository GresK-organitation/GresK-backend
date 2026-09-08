-- Presupuesto financiero consolidado de un evento (un único plan por evento, aunque
-- el evento tenga varios contratos/artistas). Base del dashboard de P&L y de la
-- validación de desviación de facturas de proveedor (cost_lines / supplier_invoices).
CREATE TABLE event_financial_plans (
    id                              UUID          NOT NULL,
    promoter_id                     UUID          NOT NULL,
    linked_event_id                 UUID          NOT NULL,
    deviation_threshold_percentage  NUMERIC(5,2)  NOT NULL DEFAULT 10,

    created_at                      TIMESTAMPTZ   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at                      TIMESTAMPTZ   NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT pk_event_financial_plans      PRIMARY KEY (id),
    CONSTRAINT fk_event_financial_plans_promoter FOREIGN KEY (promoter_id) REFERENCES promoters(id) ON DELETE RESTRICT,
    CONSTRAINT uq_event_financial_plans_event UNIQUE (linked_event_id)
);

CREATE INDEX idx_event_financial_plans_promoter ON event_financial_plans (promoter_id);

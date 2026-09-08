-- Líneas de coste (fijo/variable) de un event_financial_plan. Tabla propia (no JSONB)
-- porque supplier_invoices necesita referenciar una línea concreta con integridad
-- referencial real.
CREATE TABLE cost_lines (
    id                    UUID          NOT NULL,
    plan_id               UUID          NOT NULL,
    category              VARCHAR(20)   NOT NULL,
    subcategory           VARCHAR(30)   NOT NULL,
    description           VARCHAR(500),
    budgeted_amount       NUMERIC(12,2),
    variable_percentage   NUMERIC(5,2),
    currency              VARCHAR(3)    DEFAULT 'EUR',

    CONSTRAINT pk_cost_lines            PRIMARY KEY (id),
    CONSTRAINT fk_cost_lines_plan       FOREIGN KEY (plan_id) REFERENCES event_financial_plans(id) ON DELETE CASCADE,
    CONSTRAINT chk_cost_lines_category  CHECK (category IN ('FIXED','VARIABLE')),
    CONSTRAINT chk_cost_lines_subcategory CHECK (subcategory IN
        ('ARTIST_FEE','VENUE_RENTAL','PRODUCTION','INSURANCE','VAT_TAX',
         'SGAE_ROYALTIES','TICKETING_COMMISSION','BAR_PERCENTAGE','OTHER')),
    CONSTRAINT chk_cost_lines_amount_or_pct CHECK (budgeted_amount IS NOT NULL OR variable_percentage IS NOT NULL)
);

CREATE INDEX idx_cost_lines_plan ON cost_lines (plan_id);

-- Factura de proveedor (Accounts Payable): caché de artista, alquiler de sala, seguro,
-- producción... asociada a una línea de coste concreta del presupuesto del evento.
-- La validación de desviación es soft: se registra igual y se marca la bandera.
CREATE TABLE supplier_invoices (
    id                          UUID          NOT NULL,
    promoter_id                 UUID          NOT NULL,
    linked_event_id             UUID          NOT NULL,
    linked_cost_line_id         UUID          NOT NULL,

    supplier_name               VARCHAR(255),
    supplier_tax_id             VARCHAR(50),
    supplier_address            VARCHAR(500),
    supplier_country            VARCHAR(2),
    supplier_email              VARCHAR(255),
    supplier_invoice_number     VARCHAR(100),

    amount                      NUMERIC(12,2) NOT NULL,
    tax_amount                  NUMERIC(12,2) NOT NULL,
    total                       NUMERIC(12,2) NOT NULL,
    currency                    VARCHAR(3)    NOT NULL DEFAULT 'EUR',

    issue_date                  DATE,
    due_date                    DATE,

    budgeted_amount_snapshot    NUMERIC(12,2),
    deviation_percentage        NUMERIC(6,2),
    deviation_exceeds_threshold BOOLEAN       NOT NULL DEFAULT false,

    status                      VARCHAR(20)   NOT NULL DEFAULT 'PENDING_REVIEW',
    dispute_reason              VARCHAR(500),

    CONSTRAINT pk_supplier_invoices          PRIMARY KEY (id),
    CONSTRAINT fk_supplier_invoices_promoter FOREIGN KEY (promoter_id) REFERENCES promoters(id) ON DELETE RESTRICT,
    CONSTRAINT fk_supplier_invoices_cost_line FOREIGN KEY (linked_cost_line_id) REFERENCES cost_lines(id) ON DELETE RESTRICT,
    CONSTRAINT chk_supplier_invoices_status  CHECK (status IN ('PENDING_REVIEW','VALIDATED','DISPUTED','PAID'))
);

CREATE INDEX idx_supplier_invoices_cost_line ON supplier_invoices (linked_cost_line_id);
CREATE INDEX idx_supplier_invoices_event     ON supplier_invoices (linked_event_id);
CREATE INDEX idx_supplier_invoices_deviation ON supplier_invoices (linked_event_id) WHERE deviation_exceeds_threshold = true;

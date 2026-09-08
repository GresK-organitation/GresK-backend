-- Factura de venta (Accounts Receivable) emitida por la promotora a un cliente/patrocinador
-- por la venta de entradas u otros conceptos. lines es JSONB (snapshot inmutable de solo
-- lectura, igual que contract_versions.clauses) porque una vez emitida no se edita.
CREATE TABLE invoices (
    id                UUID          NOT NULL,
    promoter_id       UUID          NOT NULL,
    linked_event_id   UUID          NOT NULL,
    invoice_number    VARCHAR(30)   NOT NULL,

    recipient_name    VARCHAR(255),
    recipient_tax_id  VARCHAR(50),
    recipient_address VARCHAR(500),
    recipient_country VARCHAR(2),
    recipient_email   VARCHAR(255),

    lines             JSONB         NOT NULL DEFAULT '[]',
    subtotal          NUMERIC(12,2) NOT NULL,
    tax_amount        NUMERIC(12,2) NOT NULL,
    total             NUMERIC(12,2) NOT NULL,
    currency          VARCHAR(3)    NOT NULL DEFAULT 'EUR',

    status            VARCHAR(20)   NOT NULL DEFAULT 'DRAFT',
    issue_date        DATE,
    due_date          DATE,
    pdf_asset_id      VARCHAR(512),

    CONSTRAINT pk_invoices              PRIMARY KEY (id),
    CONSTRAINT fk_invoices_promoter     FOREIGN KEY (promoter_id) REFERENCES promoters(id) ON DELETE RESTRICT,
    CONSTRAINT uq_invoices_promoter_number UNIQUE (promoter_id, invoice_number),
    CONSTRAINT chk_invoices_status      CHECK (status IN ('DRAFT','ISSUED','PAID','CANCELLED'))
);

CREATE INDEX idx_invoices_event ON invoices (linked_event_id);
CREATE INDEX idx_invoices_promoter ON invoices (promoter_id);

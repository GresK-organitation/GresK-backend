-- Dinero a cobrar/pagar en una fecha, con su propia retención fiscal. Cubre tanto
-- depósitos ("50% al firmar contrato", ligados a linked_contract_id) como el saldo
-- generado automáticamente al aprobar una liquidación (ligado a linked_settlement_id).
CREATE TABLE payment_installments (
    id                      UUID          NOT NULL,
    promoter_id             UUID          NOT NULL,
    purpose                 VARCHAR(20)   NOT NULL,
    linked_contract_id      UUID,
    linked_settlement_id    UUID,

    amount                  NUMERIC(12,2) NOT NULL,
    currency                VARCHAR(3)    NOT NULL DEFAULT 'EUR',
    description             VARCHAR(500),
    due_date                DATE          NOT NULL,

    status                  VARCHAR(20)   NOT NULL DEFAULT 'PENDING',
    paid_date               DATE,
    payment_method          VARCHAR(50),

    -- Retención fiscal (IRPF / IRNR / inversión del sujeto pasivo UE), entrada manual al pagar.
    wht_kind                VARCHAR(30),
    wht_rate_percentage     NUMERIC(5,2),
    wht_tax_base            NUMERIC(12,2),
    wht_withheld_amount     NUMERIC(12,2),
    wht_exemption_reason    VARCHAR(255),

    CONSTRAINT pk_payment_installments          PRIMARY KEY (id),
    CONSTRAINT fk_payment_installments_promoter FOREIGN KEY (promoter_id) REFERENCES promoters(id) ON DELETE RESTRICT,
    CONSTRAINT fk_payment_installments_settlement FOREIGN KEY (linked_settlement_id) REFERENCES settlements(id) ON DELETE RESTRICT,
    CONSTRAINT chk_payment_installments_purpose CHECK (purpose IN ('DEPOSIT','BALANCE','AD_HOC')),
    CONSTRAINT chk_payment_installments_status  CHECK (status IN ('PENDING','PAID','CANCELLED')),
    CONSTRAINT chk_payment_installments_link    CHECK (linked_contract_id IS NOT NULL OR linked_settlement_id IS NOT NULL)
);

CREATE INDEX idx_payment_installments_contract   ON payment_installments (linked_contract_id)   WHERE linked_contract_id   IS NOT NULL;
CREATE INDEX idx_payment_installments_settlement ON payment_installments (linked_settlement_id) WHERE linked_settlement_id IS NOT NULL;
CREATE INDEX idx_payment_installments_due        ON payment_installments (due_date) WHERE status = 'PENDING';
CREATE INDEX idx_payment_installments_promoter_status ON payment_installments (promoter_id, status);

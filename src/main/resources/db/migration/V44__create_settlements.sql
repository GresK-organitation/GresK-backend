-- Resultado calculado del reparto de taquilla para un settlement_agreement. Inmutable
-- en cuanto pasa a APPROVED/PAID (patrón Stripe Charge): una liquidación mal calculada
-- se anula (VOIDED) y se recalcula en una fila nueva, nunca se parchea una ya aprobada.
CREATE TABLE settlements (
    id                              UUID          NOT NULL,
    settlement_agreement_id         UUID          NOT NULL,
    linked_event_id                 UUID          NOT NULL,
    promoter_id                     UUID          NOT NULL,

    status                          VARCHAR(20)   NOT NULL DEFAULT 'DRAFT',
    deal_type                       VARCHAR(30)   NOT NULL,
    currency                        VARCHAR(3)    NOT NULL DEFAULT 'EUR',

    gross_box_office                NUMERIC(12,2) NOT NULL,
    net_box_office                  NUMERIC(12,2) NOT NULL,
    ticketing_commission_deducted   NUMERIC(12,2),
    guaranteed_component            NUMERIC(12,2),
    percentage_component            NUMERIC(12,2),
    artist_payable_amount           NUMERIC(12,2) NOT NULL,

    calculated_at                   TIMESTAMPTZ   NOT NULL,
    approved_at                     TIMESTAMPTZ,

    CONSTRAINT pk_settlements               PRIMARY KEY (id),
    CONSTRAINT fk_settlements_agreement     FOREIGN KEY (settlement_agreement_id) REFERENCES settlement_agreements(id) ON DELETE RESTRICT,
    CONSTRAINT fk_settlements_promoter      FOREIGN KEY (promoter_id) REFERENCES promoters(id) ON DELETE RESTRICT,
    CONSTRAINT chk_settlements_status       CHECK (status IN ('DRAFT','APPROVED','PAID','VOIDED')),
    CONSTRAINT chk_settlements_deal_type    CHECK (deal_type IN
        ('FLAT_FEE','VERSUS','DOOR_SPLIT','GUARANTEED_MIN_PLUS_PERCENTAGE'))
);

CREATE INDEX idx_settlements_agreement ON settlements (settlement_agreement_id);
CREATE INDEX idx_settlements_promoter  ON settlements (promoter_id);

-- Configuración inmutable de una liquidación: snapshotea los términos del contrato
-- (feeAmount, parte B) en el momento de su creación (patrón Pretix OrderPosition /
-- Stripe Charge inmutable) para que un contrato editado después no altere una
-- liquidación ya configurada. Separado de settlements (el resultado calculado).
CREATE TABLE settlement_agreements (
    id                              UUID          NOT NULL,
    promoter_id                     UUID          NOT NULL,
    linked_contract_id              UUID          NOT NULL,
    linked_event_id                 UUID          NOT NULL,

    deal_type                       VARCHAR(30)   NOT NULL,
    guaranteed_amount               NUMERIC(12,2),
    artist_percentage               NUMERIC(5,2),
    revenue_threshold               NUMERIC(12,2),
    currency                        VARCHAR(3)    NOT NULL DEFAULT 'EUR',

    contract_fee_snapshot           NUMERIC(12,2),
    artist_name_snapshot            VARCHAR(255),
    artist_tax_id_snapshot          VARCHAR(50),
    artist_country_snapshot         VARCHAR(2),
    artist_tax_resident_snapshot    BOOLEAN       NOT NULL DEFAULT true,
    snapshotted_at                  TIMESTAMPTZ   NOT NULL,

    status                          VARCHAR(20)   NOT NULL DEFAULT 'ACTIVE',

    CONSTRAINT pk_settlement_agreements         PRIMARY KEY (id),
    CONSTRAINT fk_settlement_agreements_promoter FOREIGN KEY (promoter_id) REFERENCES promoters(id) ON DELETE RESTRICT,
    CONSTRAINT chk_settlement_agreements_deal_type CHECK (deal_type IN
        ('FLAT_FEE','VERSUS','DOOR_SPLIT','GUARANTEED_MIN_PLUS_PERCENTAGE')),
    CONSTRAINT chk_settlement_agreements_status CHECK (status IN ('ACTIVE','VOIDED'))
);

CREATE INDEX idx_settlement_agreements_promoter ON settlement_agreements (promoter_id);

-- Solo puede haber un agreement ACTIVE por contrato (regla de negocio reforzada también
-- en el caso de uso, este índice único parcial la refuerza a nivel de BD).
CREATE UNIQUE INDEX uq_settlement_agreements_active_contract ON settlement_agreements (linked_contract_id)
    WHERE status = 'ACTIVE';

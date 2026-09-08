-- Snapshot inmutable de una revisión de negociación. A diferencia de contracts (columnas
-- planas), aquí party_a/party_b/performance_details/financial_terms van como JSONB
-- porque una versión es un todo que solo se lee de vuelta completo, nunca filtrado por
-- subcampo.
CREATE TABLE contract_versions (
    id                   UUID        NOT NULL,
    contract_id          UUID        NOT NULL,
    version_number       INTEGER     NOT NULL,
    status               VARCHAR(20) NOT NULL DEFAULT 'CURRENT',
    party_a              JSONB,
    party_b              JSONB,
    performance_details  JSONB,
    financial_terms      JSONB,
    clauses              JSONB       NOT NULL DEFAULT '[]',
    change_summary       VARCHAR(1000),
    created_by           VARCHAR(255),
    created_at           TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT pk_contract_versions          PRIMARY KEY (id),
    CONSTRAINT fk_contract_versions_contract  FOREIGN KEY (contract_id) REFERENCES contracts(id) ON DELETE CASCADE,
    CONSTRAINT uq_contract_versions           UNIQUE (contract_id, version_number),
    CONSTRAINT chk_contract_versions_status   CHECK (status IN ('DRAFT_REVISION','SUPERSEDED','CURRENT'))
);

CREATE INDEX idx_contract_versions_contract ON contract_versions (contract_id, version_number DESC);

CREATE TABLE signature_envelopes (
    id                    UUID          NOT NULL,
    contract_id           UUID          NOT NULL,
    provider              VARCHAR(20)   NOT NULL,
    provider_envelope_id  VARCHAR(255),
    status                VARCHAR(20)   NOT NULL DEFAULT 'DRAFT',
    document_hash         VARCHAR(64),
    certificate_asset_id  VARCHAR(512),

    -- Lista de firmantes como JSONB: sin identidad/ciclo de vida propio fuera del
    -- envelope, mismo criterio que contracts.clauses.
    signers               JSONB         NOT NULL DEFAULT '[]',

    sent_at               TIMESTAMPTZ,
    completed_at          TIMESTAMPTZ,
    created_at            TIMESTAMPTZ   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at            TIMESTAMPTZ   NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT pk_signature_envelopes           PRIMARY KEY (id),
    CONSTRAINT fk_signature_envelopes_contract   FOREIGN KEY (contract_id) REFERENCES contracts(id) ON DELETE CASCADE,
    CONSTRAINT chk_signature_envelopes_provider  CHECK (provider IN ('SIGNATURIT','DOCUSIGN','MANUAL')),
    CONSTRAINT chk_signature_envelopes_status    CHECK (status IN ('DRAFT','SENT','DELIVERED','SIGNED','DECLINED','VOIDED','EXPIRED'))
);

CREATE INDEX idx_signature_envelopes_contract           ON signature_envelopes (contract_id);
CREATE INDEX idx_signature_envelopes_provider_envelope  ON signature_envelopes (provider_envelope_id) WHERE provider_envelope_id IS NOT NULL;

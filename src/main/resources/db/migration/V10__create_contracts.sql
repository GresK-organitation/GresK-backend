CREATE TABLE contracts (
    id                      UUID          NOT NULL,
    promoter_id             UUID          NOT NULL,
    type                    VARCHAR(20)   NOT NULL,
    status                  VARCHAR(20)   NOT NULL DEFAULT 'DRAFT',
    reference_number        VARCHAR(20)   NOT NULL,

    -- Party A (promotor)
    party_a_name            VARCHAR(255),
    party_a_tax_id          VARCHAR(50),
    party_a_address         VARCHAR(500),
    party_a_signatory_name  VARCHAR(255),
    party_a_signatory_role  VARCHAR(100),
    party_a_email           VARCHAR(255),

    -- Party B (artista / agencia / ticketera / empresa)
    party_b_name            VARCHAR(255),
    party_b_tax_id          VARCHAR(50),
    party_b_address         VARCHAR(500),
    party_b_signatory_name  VARCHAR(255),
    party_b_signatory_role  VARCHAR(100),
    party_b_email           VARCHAR(255),

    -- Detalles de actuación (nullable — solo PERFORMANCE / PRIVATE_FESTIVAL)
    perf_venue              VARCHAR(255),
    perf_event_date         DATE,
    perf_duration_minutes   INTEGER,
    perf_show_time          VARCHAR(20),

    -- Económico
    fee_amount              NUMERIC(12,2),
    fee_currency            VARCHAR(10)   NOT NULL DEFAULT 'EUR',
    payment_terms           JSONB,
    clauses                 JSONB         NOT NULL DEFAULT '[]',

    -- Administrativo
    jurisdiction            VARCHAR(255),
    contract_city           VARCHAR(100),
    contract_date           DATE,

    -- Referencias cruzadas (sin FK — referencias entre agregados)
    linked_event_id         UUID,
    linked_artist_id        UUID,
    linked_rider_id         UUID,

    -- Archivo PDF firmado (Cloudinary raw asset)
    signed_pdf_asset_id     VARCHAR(512),

    -- Share token (como el módulo de riders)
    share_token             VARCHAR(36)   UNIQUE,

    created_at              TIMESTAMPTZ   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at              TIMESTAMPTZ   NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT pk_contracts             PRIMARY KEY (id),
    CONSTRAINT fk_contracts_promoter    FOREIGN KEY (promoter_id) REFERENCES promoters(id) ON DELETE RESTRICT,
    CONSTRAINT uq_contracts_ref_number  UNIQUE (promoter_id, reference_number),
    CONSTRAINT chk_contracts_status     CHECK (status IN ('DRAFT','SENT','SIGNED','ARCHIVED','CANCELLED')),
    CONSTRAINT chk_contracts_type       CHECK (type IN ('PERFORMANCE','REPRESENTATION','TICKETING','PRIVATE_FESTIVAL'))
);

CREATE INDEX idx_contracts_promoter_id     ON contracts (promoter_id);
CREATE INDEX idx_contracts_promoter_status ON contracts (promoter_id, status);
CREATE INDEX idx_contracts_promoter_type   ON contracts (promoter_id, type);
CREATE INDEX idx_contracts_linked_event    ON contracts (linked_event_id)  WHERE linked_event_id  IS NOT NULL;
CREATE INDEX idx_contracts_linked_artist   ON contracts (linked_artist_id) WHERE linked_artist_id IS NOT NULL;
CREATE INDEX idx_contracts_signed_fee      ON contracts (promoter_id, fee_amount) WHERE status = 'SIGNED';

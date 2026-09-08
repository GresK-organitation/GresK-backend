CREATE TABLE contract_templates (
    id                          UUID         NOT NULL,
    promoter_id                 UUID,
    type                        VARCHAR(20)  NOT NULL,
    name                        VARCHAR(255) NOT NULL,
    body_markdown               TEXT         NOT NULL,
    variables                   JSONB        NOT NULL DEFAULT '[]',
    default_clause_template_ids JSONB        NOT NULL DEFAULT '[]',
    version                     INTEGER      NOT NULL DEFAULT 1,
    active                      BOOLEAN      NOT NULL DEFAULT true,
    created_at                  TIMESTAMPTZ  NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at                  TIMESTAMPTZ  NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT pk_contract_templates          PRIMARY KEY (id),
    CONSTRAINT fk_contract_templates_promoter  FOREIGN KEY (promoter_id) REFERENCES promoters(id) ON DELETE CASCADE,
    CONSTRAINT chk_contract_templates_type     CHECK (type IN ('PERFORMANCE','REPRESENTATION','TICKETING','PRIVATE_FESTIVAL'))
);

-- Un único template de sistema activo por tipo (promoter_id NULL = plantilla de sistema).
CREATE UNIQUE INDEX uq_contract_templates_system_default ON contract_templates (type) WHERE promoter_id IS NULL AND active = true;
CREATE INDEX idx_contract_templates_promoter ON contract_templates (promoter_id) WHERE promoter_id IS NOT NULL;

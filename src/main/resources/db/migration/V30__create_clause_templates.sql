-- Catálogo de cláusulas reutilizables. promoter_id NULL = plantilla de sistema
-- (visible para todas las promotoras, no editable salvo por un admin).
CREATE TABLE clause_templates (
    id                 UUID         NOT NULL,
    promoter_id        UUID,
    code               VARCHAR(60)  NOT NULL,
    category           VARCHAR(30)  NOT NULL,
    title              VARCHAR(255) NOT NULL,
    content_template   TEXT         NOT NULL,
    applicable_types   JSONB        NOT NULL DEFAULT '[]',
    jurisdiction_scope VARCHAR(10)  NOT NULL DEFAULT 'ANY',
    system_default     BOOLEAN      NOT NULL DEFAULT false,
    version            INTEGER      NOT NULL DEFAULT 1,
    active             BOOLEAN      NOT NULL DEFAULT true,
    created_at         TIMESTAMPTZ  NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at         TIMESTAMPTZ  NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT pk_clause_templates          PRIMARY KEY (id),
    CONSTRAINT fk_clause_templates_promoter  FOREIGN KEY (promoter_id) REFERENCES promoters(id) ON DELETE CASCADE,
    CONSTRAINT uq_clause_templates_code      UNIQUE (code),
    CONSTRAINT chk_clause_templates_category CHECK (category IN
        ('FORCE_MAJEURE','CANCELLATION','EXCLUSIVITY_TERRITORIAL','RIDER_ANNEX','PAYMENT','CONFIDENTIALITY','CUSTOM'))
);

CREATE INDEX idx_clause_templates_category        ON clause_templates (category);
CREATE INDEX idx_clause_templates_system_default  ON clause_templates (system_default) WHERE system_default = true;
CREATE INDEX idx_clause_templates_promoter        ON clause_templates (promoter_id) WHERE promoter_id IS NOT NULL;

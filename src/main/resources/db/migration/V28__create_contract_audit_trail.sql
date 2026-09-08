-- Append-only: la aplicación solo expone AuditTrailRepositoryPort.append(...), sin
-- update/delete, para que esta tabla sirva como evidencia en caso de disputa legal.
CREATE TABLE contract_audit_trail (
    id            UUID         NOT NULL,
    contract_id   UUID         NOT NULL,
    action        VARCHAR(40)  NOT NULL,
    actor         VARCHAR(255) NOT NULL,
    occurred_at   TIMESTAMPTZ  NOT NULL DEFAULT CURRENT_TIMESTAMP,
    ip_address    VARCHAR(45),
    user_agent    VARCHAR(500),
    metadata      JSONB,
    document_hash VARCHAR(64),

    CONSTRAINT pk_contract_audit_trail         PRIMARY KEY (id),
    CONSTRAINT fk_contract_audit_trail_contract FOREIGN KEY (contract_id) REFERENCES contracts(id) ON DELETE CASCADE
);

CREATE INDEX idx_contract_audit_trail_contract ON contract_audit_trail (contract_id, occurred_at);

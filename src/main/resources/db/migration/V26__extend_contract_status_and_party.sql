-- Extiende el ciclo de vida de contracts para reflejar los estados de un proveedor
-- de firma digital (Signaturit/DocuSign): DELIVERED (visto por el firmante) y
-- VOIDED (anulado desde el proveedor, distinto de un CANCELLED manual).
ALTER TABLE contracts DROP CONSTRAINT chk_contracts_status;
ALTER TABLE contracts ADD CONSTRAINT chk_contracts_status
    CHECK (status IN ('DRAFT','SENT','DELIVERED','SIGNED','ARCHIVED','CANCELLED','VOIDED'));

-- Residencia fiscal de las partes (necesaria para determinar si aplica IRNR).
ALTER TABLE contracts ADD COLUMN party_a_country       VARCHAR(2);
ALTER TABLE contracts ADD COLUMN party_a_tax_resident  BOOLEAN NOT NULL DEFAULT true;
ALTER TABLE contracts ADD COLUMN party_b_country       VARCHAR(2);
ALTER TABLE contracts ADD COLUMN party_b_tax_resident  BOOLEAN NOT NULL DEFAULT true;

-- Referencia al envelope de firma activo y al número de versión de negociación actual.
ALTER TABLE contracts ADD COLUMN active_signature_envelope_id UUID;
ALTER TABLE contracts ADD COLUMN current_version_number       INTEGER NOT NULL DEFAULT 1;

-- Retención fiscal (IRNR / IRPF / inversión del sujeto pasivo UE), de entrada manual.
ALTER TABLE contracts ADD COLUMN wht_type              VARCHAR(30) NOT NULL DEFAULT 'NONE';
ALTER TABLE contracts ADD COLUMN wht_rate_percentage    NUMERIC(5,2);
ALTER TABLE contracts ADD COLUMN wht_tax_base           NUMERIC(12,2);
ALTER TABLE contracts ADD COLUMN wht_withheld_amount    NUMERIC(12,2);
ALTER TABLE contracts ADD COLUMN wht_exemption_reason   VARCHAR(255);

ALTER TABLE contracts ADD CONSTRAINT chk_contracts_wht_type
    CHECK (wht_type IN ('NONE','IRNR_NON_RESIDENT','IRPF_DOMESTIC','EU_REVERSE_CHARGE'));

CREATE INDEX idx_contracts_active_envelope ON contracts (active_signature_envelope_id)
    WHERE active_signature_envelope_id IS NOT NULL;

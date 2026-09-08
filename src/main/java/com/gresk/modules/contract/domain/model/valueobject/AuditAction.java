package com.gresk.modules.contract.domain.model.valueobject;

public enum AuditAction {
    CREATED,
    UPDATED,
    SENT_FOR_SIGNATURE,
    ENVELOPE_DELIVERED,
    SIGNER_SIGNED,
    SIGNED,
    DECLINED,
    VOIDED,
    ARCHIVED,
    CANCELLED,
    PDF_GENERATED,
    SIGNED_PDF_UPLOADED,
    SHARE_LINK_GENERATED,
    VERSION_CREATED,
    CLONED
}

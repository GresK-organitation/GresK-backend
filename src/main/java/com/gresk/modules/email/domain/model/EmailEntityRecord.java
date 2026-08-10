package com.gresk.modules.email.domain.model;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * Entidad extraída por IA de un correo (fecha, importe, ítem de rider...).
 * Pertenece siempre a un {@link EmailMessage}; se elimina en cascada con él.
 */
public final class EmailEntityRecord {

    private final EmailEntityRecordId id;
    private final EmailMessageId      emailId;
    private final ExtractedEntityType entityType;
    private final Instant             createdAt;

    private String     entityKey;
    private String     entityValue;
    private String     normalizedValueJson;
    private BigDecimal confidence;
    private String     sourceSnippet;
    private boolean    requiresAction;
    private Instant    actionedAt;

    private EmailEntityRecord(EmailEntityRecordId id, EmailMessageId emailId,
                              ExtractedEntityType entityType, String entityKey, String entityValue,
                              String normalizedValueJson, BigDecimal confidence, String sourceSnippet,
                              boolean requiresAction, Instant actionedAt, Instant createdAt) {
        this.id                  = id;
        this.emailId             = emailId;
        this.entityType          = entityType;
        this.entityKey           = entityKey;
        this.entityValue         = entityValue;
        this.normalizedValueJson = normalizedValueJson;
        this.confidence          = confidence;
        this.sourceSnippet       = sourceSnippet;
        this.requiresAction      = requiresAction;
        this.actionedAt          = actionedAt;
        this.createdAt           = createdAt;
    }

    // ── Factories ─────────────────────────────────────────────────────────────

    public static EmailEntityRecord create(EmailMessageId emailId, ExtractedEntityType entityType,
                                           String entityKey, String entityValue,
                                           String normalizedValueJson, BigDecimal confidence,
                                           String sourceSnippet, boolean requiresAction) {
        return new EmailEntityRecord(
                EmailEntityRecordId.generate(), emailId,
                entityType, entityKey, entityValue,
                normalizedValueJson, confidence, sourceSnippet,
                requiresAction, null, Instant.now()
        );
    }

    public static EmailEntityRecord reconstitute(
            EmailEntityRecordId id, EmailMessageId emailId,
            ExtractedEntityType entityType, String entityKey, String entityValue,
            String normalizedValueJson, BigDecimal confidence, String sourceSnippet,
            boolean requiresAction, Instant actionedAt, Instant createdAt) {
        return new EmailEntityRecord(id, emailId, entityType, entityKey, entityValue,
                normalizedValueJson, confidence, sourceSnippet,
                requiresAction, actionedAt, createdAt);
    }

    // ── Getters ───────────────────────────────────────────────────────────────

    public EmailEntityRecordId getId()           { return id; }
    public EmailMessageId getEmailId()           { return emailId; }
    public ExtractedEntityType getEntityType()   { return entityType; }
    public String getEntityKey()                 { return entityKey; }
    public String getEntityValue()               { return entityValue; }
    public String getNormalizedValueJson()       { return normalizedValueJson; }
    public BigDecimal getConfidence()            { return confidence; }
    public String getSourceSnippet()             { return sourceSnippet; }
    public boolean isRequiresAction()            { return requiresAction; }
    public Instant getActionedAt()               { return actionedAt; }
    public Instant getCreatedAt()                { return createdAt; }
}

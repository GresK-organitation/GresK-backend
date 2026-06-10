package com.gresk.modules.email.domain.model;

import com.gresk.modules.promoter.domain.model.valueobject.PromoterId;

import java.time.Instant;

/**
 * Borrador de respuesta generado por IA para un correo, pendiente de
 * revisión por la promotora antes de enviarse.
 */
public final class EmailDraftReply {

    private final EmailDraftReplyId id;
    private final EmailMessageId    emailId;
    private final PromoterId        promoterId;
    private final String            draftType;
    private final Instant           createdAt;

    private String           subject;
    private String           body;
    private String           editedBody;
    private DraftReplyStatus status;
    private Instant          approvedAt;
    private Instant          sentAt;

    private EmailDraftReply(EmailDraftReplyId id, EmailMessageId emailId, PromoterId promoterId,
                            String draftType, String subject, String body, String editedBody,
                            DraftReplyStatus status, Instant approvedAt, Instant sentAt,
                            Instant createdAt) {
        this.id         = id;
        this.emailId    = emailId;
        this.promoterId = promoterId;
        this.draftType  = draftType;
        this.subject    = subject;
        this.body       = body;
        this.editedBody = editedBody;
        this.status     = status;
        this.approvedAt = approvedAt;
        this.sentAt     = sentAt;
        this.createdAt  = createdAt;
    }

    // ── Factories ─────────────────────────────────────────────────────────────

    public static EmailDraftReply create(EmailMessageId emailId, PromoterId promoterId,
                                         String draftType, String subject, String body) {
        return new EmailDraftReply(
                EmailDraftReplyId.generate(), emailId, promoterId,
                draftType, subject, body, null,
                DraftReplyStatus.PENDING_REVIEW, null, null,
                Instant.now()
        );
    }

    public static EmailDraftReply reconstitute(
            EmailDraftReplyId id, EmailMessageId emailId, PromoterId promoterId,
            String draftType, String subject, String body, String editedBody,
            DraftReplyStatus status, Instant approvedAt, Instant sentAt,
            Instant createdAt) {
        return new EmailDraftReply(id, emailId, promoterId, draftType, subject, body,
                editedBody, status, approvedAt, sentAt, createdAt);
    }

    // ── Getters ───────────────────────────────────────────────────────────────

    public EmailDraftReplyId getId()        { return id; }
    public EmailMessageId getEmailId()      { return emailId; }
    public PromoterId getPromoterId()       { return promoterId; }
    public String getDraftType()            { return draftType; }
    public String getSubject()              { return subject; }
    public String getBody()                 { return body; }
    public String getEditedBody()           { return editedBody; }
    public DraftReplyStatus getStatus()     { return status; }
    public Instant getApprovedAt()          { return approvedAt; }
    public Instant getSentAt()              { return sentAt; }
    public Instant getCreatedAt()           { return createdAt; }
}

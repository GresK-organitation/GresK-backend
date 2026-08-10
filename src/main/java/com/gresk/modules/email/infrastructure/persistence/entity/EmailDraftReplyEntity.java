package com.gresk.modules.email.infrastructure.persistence.entity;

import com.gresk.modules.email.domain.model.DraftReplyStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "email_draft_replies")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmailDraftReplyEntity {

    @Id
    @Column(columnDefinition = "UUID", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "email_id", nullable = false)
    private UUID emailId;

    @Column(name = "promoter_id", nullable = false)
    private UUID promoterId;

    @Column(name = "draft_type", nullable = false, length = 50)
    private String draftType;

    @Column(length = 500)
    private String subject;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String body;

    @Column(name = "edited_body", columnDefinition = "TEXT")
    private String editedBody;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private DraftReplyStatus status;

    @Column(name = "approved_at")
    private Instant approvedAt;

    @Column(name = "sent_at")
    private Instant sentAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) createdAt = Instant.now();
    }
}

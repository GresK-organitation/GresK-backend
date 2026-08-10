package com.gresk.modules.email.infrastructure.persistence.entity;

import com.gresk.modules.email.domain.model.EmailClassification;
import com.gresk.modules.email.domain.model.ProcessingStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "email_messages")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmailMessageEntity {

    @Id
    @Column(columnDefinition = "UUID", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "promoter_id", nullable = false)
    private UUID promoterId;

    @Column(name = "event_id")
    private UUID eventId;

    // ── Identificadores del proveedor (Gmail) ─────────────────────────────────
    @Column(name = "message_id_external", nullable = false, unique = true, length = 500)
    private String externalMessageId;

    @Column(name = "thread_id_external", length = 500)
    private String externalThreadId;

    // ── Cabeceras y contenido ─────────────────────────────────────────────────
    @Column(name = "from_address", nullable = false, length = 300)
    private String fromAddress;

    @Column(name = "from_name", length = 200)
    private String fromName;

    @Column(name = "to_addresses", columnDefinition = "jsonb")
    @JdbcTypeCode(SqlTypes.JSON)
    private String toAddressesJson;

    @Column(length = 500)
    private String subject;

    @Column(name = "body_text", columnDefinition = "TEXT")
    private String bodyText;

    @Column(name = "body_html", columnDefinition = "TEXT")
    private String bodyHtml;

    @Column(name = "raw_headers", columnDefinition = "jsonb")
    @JdbcTypeCode(SqlTypes.JSON)
    private String rawHeadersJson;

    // ── Clasificación por IA ──────────────────────────────────────────────────
    @Enumerated(EnumType.STRING)
    @Column(length = 50)
    private EmailClassification classification;

    @Column(name = "classification_confidence", precision = 4, scale = 2)
    private BigDecimal classificationConfidence;

    // ── Pipeline de procesamiento ─────────────────────────────────────────────
    @Enumerated(EnumType.STRING)
    @Column(name = "processing_status", nullable = false, length = 20)
    private ProcessingStatus processingStatus;

    @Column(name = "processing_attempts", nullable = false)
    private int processingAttempts;

    @Column(name = "last_attempt_at")
    private Instant lastAttemptAt;

    @Column(name = "processed_at")
    private Instant processedAt;

    @Column(name = "received_at", nullable = false)
    private Instant receivedAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) createdAt = Instant.now();
    }
}

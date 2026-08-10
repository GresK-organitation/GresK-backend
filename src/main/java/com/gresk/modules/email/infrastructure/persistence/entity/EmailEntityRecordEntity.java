package com.gresk.modules.email.infrastructure.persistence.entity;

import com.gresk.modules.email.domain.model.ExtractedEntityType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "email_entities")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmailEntityRecordEntity {

    @Id
    @Column(columnDefinition = "UUID", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "email_id", nullable = false)
    private UUID emailId;

    @Enumerated(EnumType.STRING)
    @Column(name = "entity_type", nullable = false, length = 50)
    private ExtractedEntityType entityType;

    @Column(name = "entity_key", length = 200)
    private String entityKey;

    @Column(name = "entity_value", nullable = false, columnDefinition = "TEXT")
    private String entityValue;

    @Column(name = "entity_value_normalized", columnDefinition = "jsonb")
    @JdbcTypeCode(SqlTypes.JSON)
    private String normalizedValueJson;

    @Column(precision = 4, scale = 2)
    private BigDecimal confidence;

    @Column(name = "source_snippet", columnDefinition = "TEXT")
    private String sourceSnippet;

    @Column(name = "requires_action", nullable = false)
    private boolean requiresAction;

    @Column(name = "actioned_at")
    private Instant actionedAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) createdAt = Instant.now();
    }
}

package com.gresk.modules.contract.infrastructure.persistence.entity;

import com.gresk.modules.contract.domain.model.valueobject.AuditAction;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "contract_audit_trail")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ContractAuditTrailEntity {

    @Id
    @Column(columnDefinition = "UUID", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "contract_id", nullable = false)
    private UUID contractId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 40)
    private AuditAction action;

    @Column(nullable = false, length = 255)
    private String actor;

    @Column(name = "occurred_at", nullable = false, updatable = false)
    private Instant occurredAt;

    @Column(name = "ip_address", length = 45) private String ipAddress;
    @Column(name = "user_agent", length = 500) private String userAgent;

    @Column(name = "metadata", columnDefinition = "jsonb")
    @JdbcTypeCode(SqlTypes.JSON)
    private String metadataJson;

    @Column(name = "document_hash", length = 64) private String documentHash;

    @PrePersist
    protected void onCreate() {
        if (occurredAt == null) occurredAt = Instant.now();
    }
}

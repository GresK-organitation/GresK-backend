package com.gresk.modules.contract.infrastructure.persistence.entity;

import com.gresk.modules.contract.domain.model.ContractVersionStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "contract_versions")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ContractVersionEntity {

    @Id
    @Column(columnDefinition = "UUID", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "contract_id", nullable = false)
    private UUID contractId;

    @Column(name = "version_number", nullable = false)
    private int versionNumber;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ContractVersionStatus status;

    @Column(name = "party_a", columnDefinition = "jsonb") @JdbcTypeCode(SqlTypes.JSON) private String partyAJson;
    @Column(name = "party_b", columnDefinition = "jsonb") @JdbcTypeCode(SqlTypes.JSON) private String partyBJson;
    @Column(name = "performance_details", columnDefinition = "jsonb") @JdbcTypeCode(SqlTypes.JSON) private String performanceDetailsJson;
    @Column(name = "financial_terms", columnDefinition = "jsonb") @JdbcTypeCode(SqlTypes.JSON) private String financialTermsJson;
    @Column(name = "clauses", columnDefinition = "jsonb", nullable = false) @JdbcTypeCode(SqlTypes.JSON) private String clausesJson;

    @Column(name = "change_summary", length = 1000) private String changeSummary;
    @Column(name = "created_by", length = 255) private String createdBy;

    @Column(name = "created_at", nullable = false, updatable = false) private Instant createdAt;

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) createdAt = Instant.now();
    }
}

package com.gresk.modules.contract.infrastructure.persistence.entity;

import com.gresk.modules.contract.domain.model.valueobject.EnvelopeProvider;
import com.gresk.modules.contract.domain.model.valueobject.EnvelopeStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "signature_envelopes")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SignatureEnvelopeEntity {

    @Id
    @Column(columnDefinition = "UUID", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "contract_id", nullable = false)
    private UUID contractId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EnvelopeProvider provider;

    @Column(name = "provider_envelope_id", length = 255)
    private String providerEnvelopeId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EnvelopeStatus status;

    @Column(name = "document_hash", length = 64)
    private String documentHash;

    @Column(name = "certificate_asset_id", length = 512)
    private String certificateAssetId;

    @Column(name = "signers", columnDefinition = "jsonb", nullable = false)
    @JdbcTypeCode(SqlTypes.JSON)
    private String signersJson;

    @Column(name = "sent_at")      private Instant sentAt;
    @Column(name = "completed_at") private Instant completedAt;

    @Column(name = "created_at", nullable = false, updatable = false) private Instant createdAt;
    @Column(name = "updated_at", nullable = false)                    private Instant updatedAt;

    @PrePersist
    protected void onCreate() {
        Instant now = Instant.now();
        if (createdAt == null) createdAt = now;
        updatedAt = now;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = Instant.now();
    }
}

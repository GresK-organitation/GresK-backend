package com.gresk.modules.contract.infrastructure.persistence.entity;

import com.gresk.modules.contract.domain.model.ContractStatus;
import com.gresk.modules.contract.domain.model.ContractType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "contracts")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ContractEntity {

    @Id
    @Column(columnDefinition = "UUID", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "promoter_id", nullable = false)
    private UUID promoterId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ContractType type;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ContractStatus status;

    @Column(name = "reference_number", nullable = false, length = 20)
    private String referenceNumber;

    // ── Party A ───────────────────────────────────────────────────────────────
    @Column(name = "party_a_name",           length = 255) private String partyAName;
    @Column(name = "party_a_tax_id",         length = 50)  private String partyATaxId;
    @Column(name = "party_a_address",        length = 500) private String partyAAddress;
    @Column(name = "party_a_signatory_name", length = 255) private String partyASignatoryName;
    @Column(name = "party_a_signatory_role", length = 100) private String partyASignatoryRole;
    @Column(name = "party_a_email",          length = 255) private String partyAEmail;
    @Column(name = "party_a_country",        length = 2)   private String partyACountry;
    @Column(name = "party_a_tax_resident")                 private boolean partyATaxResident = true;

    // ── Party B ───────────────────────────────────────────────────────────────
    @Column(name = "party_b_name",           length = 255) private String partyBName;
    @Column(name = "party_b_tax_id",         length = 50)  private String partyBTaxId;
    @Column(name = "party_b_address",        length = 500) private String partyBAddress;
    @Column(name = "party_b_signatory_name", length = 255) private String partyBSignatoryName;
    @Column(name = "party_b_signatory_role", length = 100) private String partyBSignatoryRole;
    @Column(name = "party_b_email",          length = 255) private String partyBEmail;
    @Column(name = "party_b_country",        length = 2)   private String partyBCountry;
    @Column(name = "party_b_tax_resident")                 private boolean partyBTaxResident = true;

    // ── Performance details ───────────────────────────────────────────────────
    @Column(name = "perf_venue",            length = 255) private String    perfVenue;
    @Column(name = "perf_event_date")                     private LocalDate perfEventDate;
    @Column(name = "perf_duration_minutes")               private Integer   perfDurationMinutes;
    @Column(name = "perf_show_time",        length = 20)  private String    perfShowTime;

    // ── Financial ─────────────────────────────────────────────────────────────
    @Column(name = "fee_amount",   precision = 12, scale = 2) private BigDecimal feeAmount;
    @Column(name = "fee_currency", length = 10)               private String     feeCurrency;

    @Column(name = "payment_terms", columnDefinition = "jsonb")
    @JdbcTypeCode(SqlTypes.JSON)
    private String paymentTermsJson;

    @Column(name = "clauses", columnDefinition = "jsonb", nullable = false)
    @JdbcTypeCode(SqlTypes.JSON)
    private String clausesJson;

    // ── Withholding tax (IRNR / IRPF / EU reverse charge), entrada manual ────────
    @Column(name = "wht_type", length = 30, nullable = false) private String whtType = "NONE";
    @Column(name = "wht_rate_percentage",  precision = 5, scale = 2)  private BigDecimal whtRatePercentage;
    @Column(name = "wht_tax_base",         precision = 12, scale = 2) private BigDecimal whtTaxBase;
    @Column(name = "wht_withheld_amount",  precision = 12, scale = 2) private BigDecimal whtWithheldAmount;
    @Column(name = "wht_exemption_reason", length = 255)              private String     whtExemptionReason;

    // ── Administrative ────────────────────────────────────────────────────────
    @Column(name = "jurisdiction",   length = 255) private String    jurisdiction;
    @Column(name = "contract_city",  length = 100) private String    contractCity;
    @Column(name = "contract_date")                private LocalDate contractDate;

    // ── Cross-aggregate links ─────────────────────────────────────────────────
    @Column(name = "linked_event_id")  private UUID linkedEventId;
    @Column(name = "linked_artist_id") private UUID linkedArtistId;
    @Column(name = "linked_rider_id")  private UUID linkedRiderId;

    // ── Files & sharing ───────────────────────────────────────────────────────
    @Column(name = "signed_pdf_asset_id", length = 512) private String signedPdfAssetId;
    @Column(name = "share_token",         length = 36, unique = true) private String shareToken;

    @Column(name = "active_signature_envelope_id") private UUID activeSignatureEnvelopeId;
    @Column(name = "current_version_number", nullable = false)  private int currentVersionNumber = 1;

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

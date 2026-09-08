package com.gresk.modules.finance.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "settlement_agreements")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SettlementAgreementEntity {

    @Id
    @Column(columnDefinition = "UUID", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "promoter_id", nullable = false)
    private UUID promoterId;

    @Column(name = "linked_contract_id", nullable = false)
    private UUID linkedContractId;

    @Column(name = "linked_event_id", nullable = false)
    private UUID linkedEventId;

    @Column(name = "deal_type", nullable = false, length = 30)
    private String dealType;

    @Column(name = "guaranteed_amount", precision = 12, scale = 2)
    private BigDecimal guaranteedAmount;

    @Column(name = "artist_percentage", precision = 5, scale = 2)
    private BigDecimal artistPercentage;

    @Column(name = "revenue_threshold", precision = 12, scale = 2)
    private BigDecimal revenueThreshold;

    @Column(length = 3)
    private String currency;

    @Column(name = "contract_fee_snapshot", precision = 12, scale = 2)
    private BigDecimal contractFeeSnapshot;

    @Column(name = "artist_name_snapshot", length = 255)
    private String artistNameSnapshot;

    @Column(name = "artist_tax_id_snapshot", length = 50)
    private String artistTaxIdSnapshot;

    @Column(name = "artist_country_snapshot", length = 2)
    private String artistCountrySnapshot;

    @Column(name = "artist_tax_resident_snapshot", nullable = false)
    private boolean artistTaxResidentSnapshot;

    @Column(name = "snapshotted_at", nullable = false)
    private Instant snapshottedAt;

    @Column(nullable = false, length = 20)
    private String status;
}

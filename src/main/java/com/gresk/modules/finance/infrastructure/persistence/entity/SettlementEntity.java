package com.gresk.modules.finance.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "settlements")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SettlementEntity {

    @Id
    @Column(columnDefinition = "UUID", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "settlement_agreement_id", nullable = false)
    private UUID settlementAgreementId;

    @Column(name = "linked_event_id", nullable = false)
    private UUID linkedEventId;

    @Column(name = "promoter_id", nullable = false)
    private UUID promoterId;

    @Column(nullable = false, length = 20)
    private String status;

    @Column(name = "deal_type", nullable = false, length = 30)
    private String dealType;

    @Column(length = 3)
    private String currency;

    @Column(name = "gross_box_office", precision = 12, scale = 2, nullable = false)
    private BigDecimal grossBoxOffice;

    @Column(name = "net_box_office", precision = 12, scale = 2, nullable = false)
    private BigDecimal netBoxOffice;

    @Column(name = "ticketing_commission_deducted", precision = 12, scale = 2)
    private BigDecimal ticketingCommissionDeducted;

    @Column(name = "guaranteed_component", precision = 12, scale = 2)
    private BigDecimal guaranteedComponent;

    @Column(name = "percentage_component", precision = 12, scale = 2)
    private BigDecimal percentageComponent;

    @Column(name = "artist_payable_amount", precision = 12, scale = 2, nullable = false)
    private BigDecimal artistPayableAmount;

    @Column(name = "calculated_at", nullable = false)
    private Instant calculatedAt;

    @Column(name = "approved_at")
    private Instant approvedAt;
}

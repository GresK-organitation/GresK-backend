package com.gresk.modules.finance.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "payment_installments")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentInstallmentEntity {

    @Id
    @Column(columnDefinition = "UUID", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "promoter_id", nullable = false)
    private UUID promoterId;

    @Column(nullable = false, length = 20)
    private String purpose;

    @Column(name = "linked_contract_id")
    private UUID linkedContractId;

    @Column(name = "linked_settlement_id")
    private UUID linkedSettlementId;

    @Column(precision = 12, scale = 2, nullable = false)
    private BigDecimal amount;

    @Column(length = 3, nullable = false)
    private String currency;

    @Column(length = 500)
    private String description;

    @Column(name = "due_date", nullable = false)
    private LocalDate dueDate;

    @Column(nullable = false, length = 20)
    private String status;

    @Column(name = "paid_date")
    private LocalDate paidDate;

    @Column(name = "payment_method", length = 50)
    private String paymentMethod;

    // ── Withholding tax, entrada manual en el momento del pago ──────────────────
    @Column(name = "wht_kind", length = 30) private String     whtKind;
    @Column(name = "wht_rate_percentage",  precision = 5, scale = 2)  private BigDecimal whtRatePercentage;
    @Column(name = "wht_tax_base",         precision = 12, scale = 2) private BigDecimal whtTaxBase;
    @Column(name = "wht_withheld_amount",  precision = 12, scale = 2) private BigDecimal whtWithheldAmount;
    @Column(name = "wht_exemption_reason", length = 255)              private String     whtExemptionReason;
}

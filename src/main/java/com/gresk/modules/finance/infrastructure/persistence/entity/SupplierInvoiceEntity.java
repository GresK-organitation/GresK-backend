package com.gresk.modules.finance.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "supplier_invoices")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SupplierInvoiceEntity {

    @Id
    @Column(columnDefinition = "UUID", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "promoter_id", nullable = false)
    private UUID promoterId;

    @Column(name = "linked_event_id", nullable = false)
    private UUID linkedEventId;

    @Column(name = "linked_cost_line_id", nullable = false)
    private UUID linkedCostLineId;

    // ── Supplier ──────────────────────────────────────────────────────────────
    @Column(name = "supplier_name",    length = 255) private String supplierName;
    @Column(name = "supplier_tax_id",  length = 50)  private String supplierTaxId;
    @Column(name = "supplier_address", length = 500) private String supplierAddress;
    @Column(name = "supplier_country", length = 2)   private String supplierCountry;
    @Column(name = "supplier_email",   length = 255) private String supplierEmail;

    @Column(name = "supplier_invoice_number", length = 100) private String supplierInvoiceNumber;

    @Column(precision = 12, scale = 2, nullable = false) private BigDecimal amount;
    @Column(name = "tax_amount", precision = 12, scale = 2, nullable = false) private BigDecimal taxAmount;
    @Column(precision = 12, scale = 2, nullable = false) private BigDecimal total;
    @Column(length = 3, nullable = false) private String currency;

    @Column(name = "issue_date") private LocalDate issueDate;
    @Column(name = "due_date")   private LocalDate dueDate;

    @Column(name = "budgeted_amount_snapshot", precision = 12, scale = 2) private BigDecimal budgetedAmountSnapshot;
    @Column(name = "deviation_percentage", precision = 6, scale = 2)      private BigDecimal deviationPercentage;
    @Column(name = "deviation_exceeds_threshold", nullable = false)      private boolean deviationExceedsThreshold;

    @Column(nullable = false, length = 20) private String status;

    @Column(name = "dispute_reason", length = 500) private String disputeReason;
}

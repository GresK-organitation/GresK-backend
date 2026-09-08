package com.gresk.modules.finance.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "invoices")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InvoiceEntity {

    @Id
    @Column(columnDefinition = "UUID", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "promoter_id", nullable = false)
    private UUID promoterId;

    @Column(name = "linked_event_id", nullable = false)
    private UUID linkedEventId;

    @Column(name = "invoice_number", nullable = false, length = 30)
    private String invoiceNumber;

    // ── Recipient ─────────────────────────────────────────────────────────────
    @Column(name = "recipient_name",    length = 255) private String recipientName;
    @Column(name = "recipient_tax_id",  length = 50)  private String recipientTaxId;
    @Column(name = "recipient_address", length = 500) private String recipientAddress;
    @Column(name = "recipient_country", length = 2)   private String recipientCountry;
    @Column(name = "recipient_email",   length = 255) private String recipientEmail;

    @Column(name = "lines", columnDefinition = "jsonb", nullable = false)
    @JdbcTypeCode(SqlTypes.JSON)
    private String linesJson;

    @Column(precision = 12, scale = 2, nullable = false) private BigDecimal subtotal;
    @Column(name = "tax_amount", precision = 12, scale = 2, nullable = false) private BigDecimal taxAmount;
    @Column(precision = 12, scale = 2, nullable = false) private BigDecimal total;
    @Column(length = 3, nullable = false) private String currency;

    @Column(nullable = false, length = 20) private String status;

    @Column(name = "issue_date") private LocalDate issueDate;
    @Column(name = "due_date")   private LocalDate dueDate;

    @Column(name = "pdf_asset_id", length = 512) private String pdfAssetId;
}

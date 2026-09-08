package com.gresk.modules.finance.domain.model;

import com.gresk.modules.finance.domain.exception.InvalidInvoiceStatusTransitionException;
import com.gresk.modules.finance.domain.model.valueobject.InvoiceLine;
import com.gresk.modules.finance.domain.model.valueobject.InvoiceParty;
import com.gresk.modules.promoter.domain.model.valueobject.PromoterId;
import com.gresk.shared.domain.valueobject.Money;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/** Factura de venta (Accounts Receivable), emitida por la promotora a un cliente/patrocinador. */
public final class Invoice {

    private final InvoiceId       id;
    private final PromoterId      promoterId;
    private final UUID            linkedEventId;
    private final String          invoiceNumber;
    private final InvoiceParty    recipient;
    private final List<InvoiceLine> lines;
    private final Money           subtotal;
    private final Money           taxAmount;
    private final Money           total;
    private final LocalDate       issueDate;
    private final LocalDate       dueDate;

    private InvoiceStatus status;
    private String         pdfAssetId;

    private Invoice(InvoiceId id, PromoterId promoterId, UUID linkedEventId, String invoiceNumber,
                     InvoiceParty recipient, List<InvoiceLine> lines, Money subtotal, Money taxAmount,
                     Money total, LocalDate issueDate, LocalDate dueDate, InvoiceStatus status, String pdfAssetId) {
        this.id             = id;
        this.promoterId     = promoterId;
        this.linkedEventId  = linkedEventId;
        this.invoiceNumber  = invoiceNumber;
        this.recipient      = recipient;
        this.lines          = List.copyOf(lines);
        this.subtotal       = subtotal;
        this.taxAmount      = taxAmount;
        this.total          = total;
        this.issueDate      = issueDate;
        this.dueDate        = dueDate;
        this.status         = status;
        this.pdfAssetId     = pdfAssetId;
    }

    public static Invoice create(PromoterId promoterId, UUID linkedEventId, String invoiceNumber,
                                  InvoiceParty recipient, List<InvoiceLine> lines,
                                  LocalDate issueDate, LocalDate dueDate) {
        if (lines == null || lines.isEmpty()) throw new IllegalArgumentException("an invoice requires at least one line");
        String currency = lines.get(0).unitPrice().currency();
        Money subtotal = lines.stream().map(InvoiceLine::lineSubtotal).reduce(Money.zero(currency), Money::add);
        Money tax      = lines.stream().map(InvoiceLine::lineTax).reduce(Money.zero(currency), Money::add);
        Money total    = subtotal.add(tax);

        return new Invoice(InvoiceId.generate(), promoterId, linkedEventId, invoiceNumber, recipient, lines,
                subtotal, tax, total, issueDate, dueDate, InvoiceStatus.DRAFT, null);
    }

    public static Invoice reconstitute(InvoiceId id, PromoterId promoterId, UUID linkedEventId, String invoiceNumber,
                                        InvoiceParty recipient, List<InvoiceLine> lines, Money subtotal,
                                        Money taxAmount, Money total, LocalDate issueDate, LocalDate dueDate,
                                        InvoiceStatus status, String pdfAssetId) {
        return new Invoice(id, promoterId, linkedEventId, invoiceNumber, recipient, lines, subtotal, taxAmount,
                total, issueDate, dueDate, status, pdfAssetId);
    }

    public void issue() {
        if (status != InvoiceStatus.DRAFT) {
            throw new InvalidInvoiceStatusTransitionException("Cannot issue an invoice in status: " + status);
        }
        this.status = InvoiceStatus.ISSUED;
    }

    public void markPaid() {
        if (status != InvoiceStatus.ISSUED) {
            throw new InvalidInvoiceStatusTransitionException("Cannot mark paid an invoice in status: " + status);
        }
        this.status = InvoiceStatus.PAID;
    }

    public void cancel() {
        if (status == InvoiceStatus.PAID || status == InvoiceStatus.CANCELLED) {
            throw new InvalidInvoiceStatusTransitionException("Cannot cancel an invoice in status: " + status);
        }
        this.status = InvoiceStatus.CANCELLED;
    }

    public void attachPdfAssetId(String pdfAssetId) {
        this.pdfAssetId = pdfAssetId;
    }

    public InvoiceId           getId()             { return id; }
    public PromoterId          getPromoterId()     { return promoterId; }
    public UUID                getLinkedEventId()  { return linkedEventId; }
    public String              getInvoiceNumber()  { return invoiceNumber; }
    public InvoiceParty        getRecipient()      { return recipient; }
    public List<InvoiceLine>   getLines()          { return lines; }
    public Money                getSubtotal()       { return subtotal; }
    public Money                getTaxAmount()      { return taxAmount; }
    public Money                getTotal()          { return total; }
    public LocalDate            getIssueDate()      { return issueDate; }
    public LocalDate            getDueDate()        { return dueDate; }
    public InvoiceStatus        getStatus()         { return status; }
    public String                getPdfAssetId()     { return pdfAssetId; }
}

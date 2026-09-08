package com.gresk.modules.finance.domain.model;

import com.gresk.modules.finance.domain.exception.CostLineNotFoundException;
import com.gresk.modules.finance.domain.exception.InvalidSupplierInvoiceStatusTransitionException;
import com.gresk.modules.finance.domain.model.valueobject.CostLine;
import com.gresk.modules.finance.domain.model.valueobject.CostLineId;
import com.gresk.modules.finance.domain.model.valueobject.InvoiceParty;
import com.gresk.modules.promoter.domain.model.valueobject.PromoterId;
import com.gresk.shared.domain.valueobject.Money;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.UUID;

/**
 * Factura de proveedor (Accounts Payable): caché de artista, alquiler de sala, seguro,
 * producción... La validación de desviación contra el presupuesto es soft: se registra
 * igual y se marca la bandera, no bloquea el registro contable real.
 */
public final class SupplierInvoice {

    private final SupplierInvoiceId id;
    private final PromoterId        promoterId;
    private final UUID              linkedEventId;
    private final CostLineId        linkedCostLineId;
    private final InvoiceParty      supplier;
    private final String            supplierInvoiceNumber;
    private final Money             amount;
    private final Money             taxAmount;
    private final Money             total;
    private final LocalDate         issueDate;
    private final LocalDate         dueDate;
    private final Money             budgetedAmountSnapshot;
    private final BigDecimal        deviationPercentage;
    private final boolean           deviationExceedsThreshold;

    private SupplierInvoiceStatus status;
    private String                 disputeReason;

    private SupplierInvoice(SupplierInvoiceId id, PromoterId promoterId, UUID linkedEventId,
                             CostLineId linkedCostLineId, InvoiceParty supplier, String supplierInvoiceNumber,
                             Money amount, Money taxAmount, Money total, LocalDate issueDate, LocalDate dueDate,
                             Money budgetedAmountSnapshot, BigDecimal deviationPercentage,
                             boolean deviationExceedsThreshold, SupplierInvoiceStatus status, String disputeReason) {
        this.id                        = id;
        this.promoterId                = promoterId;
        this.linkedEventId             = linkedEventId;
        this.linkedCostLineId          = linkedCostLineId;
        this.supplier                  = supplier;
        this.supplierInvoiceNumber     = supplierInvoiceNumber;
        this.amount                    = amount;
        this.taxAmount                 = taxAmount;
        this.total                     = total;
        this.issueDate                 = issueDate;
        this.dueDate                   = dueDate;
        this.budgetedAmountSnapshot    = budgetedAmountSnapshot;
        this.deviationPercentage       = deviationPercentage;
        this.deviationExceedsThreshold = deviationExceedsThreshold;
        this.status                    = status;
        this.disputeReason             = disputeReason;
    }

    public static SupplierInvoice associateToBudget(
            PromoterId promoterId, UUID linkedEventId, CostLineId linkedCostLineId, InvoiceParty supplier,
            String supplierInvoiceNumber, Money amount, Money taxAmount, LocalDate issueDate, LocalDate dueDate,
            EventFinancialPlan plan) {

        CostLine costLine = plan.getCostLines().stream()
                .filter(cl -> cl.id().equals(linkedCostLineId))
                .findFirst()
                .orElseThrow(() -> new CostLineNotFoundException(linkedCostLineId.toString()));

        Money total = amount.add(taxAmount);
        Money budgetedAmountSnapshot = costLine.budgetedAmount();
        BigDecimal deviationPercentage = null;
        boolean exceedsThreshold = false;

        if (budgetedAmountSnapshot != null && budgetedAmountSnapshot.amount().signum() != 0) {
            deviationPercentage = total.amount().subtract(budgetedAmountSnapshot.amount())
                    .divide(budgetedAmountSnapshot.amount(), 4, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100))
                    .setScale(2, RoundingMode.HALF_UP);
            exceedsThreshold = deviationPercentage.abs().compareTo(plan.getDeviationThresholdPercentage()) > 0;
        }

        return new SupplierInvoice(SupplierInvoiceId.generate(), promoterId, linkedEventId, linkedCostLineId,
                supplier, supplierInvoiceNumber, amount, taxAmount, total, issueDate, dueDate,
                budgetedAmountSnapshot, deviationPercentage, exceedsThreshold, SupplierInvoiceStatus.PENDING_REVIEW, null);
    }

    public static SupplierInvoice reconstitute(
            SupplierInvoiceId id, PromoterId promoterId, UUID linkedEventId, CostLineId linkedCostLineId,
            InvoiceParty supplier, String supplierInvoiceNumber, Money amount, Money taxAmount, Money total,
            LocalDate issueDate, LocalDate dueDate, Money budgetedAmountSnapshot, BigDecimal deviationPercentage,
            boolean deviationExceedsThreshold, SupplierInvoiceStatus status, String disputeReason) {
        return new SupplierInvoice(id, promoterId, linkedEventId, linkedCostLineId, supplier, supplierInvoiceNumber,
                amount, taxAmount, total, issueDate, dueDate, budgetedAmountSnapshot, deviationPercentage,
                deviationExceedsThreshold, status, disputeReason);
    }

    public void validate() {
        if (status != SupplierInvoiceStatus.PENDING_REVIEW) {
            throw new InvalidSupplierInvoiceStatusTransitionException(
                    "Cannot validate a supplier invoice in status: " + status);
        }
        this.status = SupplierInvoiceStatus.VALIDATED;
    }

    public void dispute(String reason) {
        if (status == SupplierInvoiceStatus.PAID) {
            throw new InvalidSupplierInvoiceStatusTransitionException("Cannot dispute a supplier invoice already PAID");
        }
        this.status        = SupplierInvoiceStatus.DISPUTED;
        this.disputeReason = reason;
    }

    public void markPaid() {
        if (status != SupplierInvoiceStatus.VALIDATED) {
            throw new InvalidSupplierInvoiceStatusTransitionException(
                    "Cannot mark paid a supplier invoice in status: " + status);
        }
        this.status = SupplierInvoiceStatus.PAID;
    }

    public SupplierInvoiceId     getId()                        { return id; }
    public PromoterId            getPromoterId()                { return promoterId; }
    public UUID                  getLinkedEventId()             { return linkedEventId; }
    public CostLineId            getLinkedCostLineId()          { return linkedCostLineId; }
    public InvoiceParty          getSupplier()                  { return supplier; }
    public String                getSupplierInvoiceNumber()     { return supplierInvoiceNumber; }
    public Money                  getAmount()                    { return amount; }
    public Money                  getTaxAmount()                 { return taxAmount; }
    public Money                  getTotal()                     { return total; }
    public LocalDate              getIssueDate()                 { return issueDate; }
    public LocalDate              getDueDate()                   { return dueDate; }
    public Money                  getBudgetedAmountSnapshot()    { return budgetedAmountSnapshot; }
    public BigDecimal             getDeviationPercentage()       { return deviationPercentage; }
    public boolean                isDeviationExceedsThreshold()  { return deviationExceedsThreshold; }
    public SupplierInvoiceStatus  getStatus()                    { return status; }
    public String                 getDisputeReason()             { return disputeReason; }
}

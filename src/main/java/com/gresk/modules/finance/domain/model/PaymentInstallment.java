package com.gresk.modules.finance.domain.model;

import com.gresk.modules.finance.domain.exception.InvalidInstallmentStatusTransitionException;
import com.gresk.modules.finance.domain.model.valueobject.WithholdingApplication;
import com.gresk.modules.promoter.domain.model.valueobject.PromoterId;
import com.gresk.shared.domain.valueobject.Money;

import java.time.LocalDate;
import java.util.UUID;

/**
 * Dinero que se debe cobrar/pagar en una fecha, con su propia retención. Une depósitos
 * ("50% al firmar") y el saldo tras una liquidación (BALANCE, creado automáticamente al
 * aprobar un Settlement) en un único agregado.
 */
public final class PaymentInstallment {

    private final PaymentInstallmentId id;
    private final PromoterId           promoterId;
    private final InstallmentPurpose   purpose;
    private final UUID                 linkedContractId;
    private final UUID                 linkedSettlementId;
    private final Money                amount;
    private final String               description;
    private final LocalDate            dueDate;

    private InstallmentStatus       status;
    private LocalDate               paidDate;
    private String                  paymentMethod;
    private WithholdingApplication  withholding;

    private PaymentInstallment(PaymentInstallmentId id, PromoterId promoterId, InstallmentPurpose purpose,
                                UUID linkedContractId, UUID linkedSettlementId, Money amount, String description,
                                LocalDate dueDate, InstallmentStatus status, LocalDate paidDate,
                                String paymentMethod, WithholdingApplication withholding) {
        this.id                 = id;
        this.promoterId         = promoterId;
        this.purpose            = purpose;
        this.linkedContractId   = linkedContractId;
        this.linkedSettlementId = linkedSettlementId;
        this.amount             = amount;
        this.description        = description;
        this.dueDate            = dueDate;
        this.status             = status;
        this.paidDate           = paidDate;
        this.paymentMethod      = paymentMethod;
        this.withholding        = withholding;
    }

    public static PaymentInstallment scheduleDeposit(PromoterId promoterId, UUID linkedContractId,
                                                       Money amount, LocalDate dueDate, String description) {
        return new PaymentInstallment(PaymentInstallmentId.generate(), promoterId, InstallmentPurpose.DEPOSIT,
                linkedContractId, null, amount, description, dueDate, InstallmentStatus.PENDING,
                null, null, null);
    }

    public static PaymentInstallment scheduleBalance(PromoterId promoterId, UUID linkedSettlementId,
                                                       Money amount, LocalDate dueDate, String description) {
        return new PaymentInstallment(PaymentInstallmentId.generate(), promoterId, InstallmentPurpose.BALANCE,
                null, linkedSettlementId, amount, description, dueDate, InstallmentStatus.PENDING,
                null, null, null);
    }

    public static PaymentInstallment reconstitute(PaymentInstallmentId id, PromoterId promoterId,
                                                   InstallmentPurpose purpose, UUID linkedContractId,
                                                   UUID linkedSettlementId, Money amount, String description,
                                                   LocalDate dueDate, InstallmentStatus status, LocalDate paidDate,
                                                   String paymentMethod, WithholdingApplication withholding) {
        return new PaymentInstallment(id, promoterId, purpose, linkedContractId, linkedSettlementId, amount,
                description, dueDate, status, paidDate, paymentMethod, withholding);
    }

    public void markPaid(LocalDate paidDate, String paymentMethod, WithholdingApplication withholding) {
        if (status != InstallmentStatus.PENDING) {
            throw new InvalidInstallmentStatusTransitionException(
                    "Cannot mark paid an installment in status: " + status);
        }
        this.status        = InstallmentStatus.PAID;
        this.paidDate      = paidDate;
        this.paymentMethod = paymentMethod;
        this.withholding   = withholding;
    }

    public void cancel() {
        if (status == InstallmentStatus.PAID) {
            throw new InvalidInstallmentStatusTransitionException("Cannot cancel an installment already PAID");
        }
        this.status = InstallmentStatus.CANCELLED;
    }

    /** Nunca persistido como OVERDUE: se deriva comparando dueDate con la fecha dada. */
    public InstallmentStatus getEffectiveStatus(LocalDate asOf) {
        if (status == InstallmentStatus.PENDING && dueDate.isBefore(asOf)) {
            return InstallmentStatus.OVERDUE;
        }
        return status;
    }

    public PaymentInstallmentId  getId()                 { return id; }
    public PromoterId            getPromoterId()         { return promoterId; }
    public InstallmentPurpose    getPurpose()             { return purpose; }
    public UUID                  getLinkedContractId()   { return linkedContractId; }
    public UUID                  getLinkedSettlementId() { return linkedSettlementId; }
    public Money                 getAmount()             { return amount; }
    public String                getDescription()         { return description; }
    public LocalDate             getDueDate()             { return dueDate; }
    public InstallmentStatus     getStatus()              { return status; }
    public LocalDate             getPaidDate()            { return paidDate; }
    public String                getPaymentMethod()       { return paymentMethod; }
    public WithholdingApplication getWithholding()        { return withholding; }
}

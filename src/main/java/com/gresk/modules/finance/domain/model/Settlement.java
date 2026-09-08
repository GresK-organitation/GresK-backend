package com.gresk.modules.finance.domain.model;

import com.gresk.modules.finance.domain.exception.InvalidSettlementStatusTransitionException;
import com.gresk.modules.finance.domain.model.valueobject.SettlementBreakdown;
import com.gresk.modules.promoter.domain.model.valueobject.PromoterId;
import com.gresk.shared.domain.valueobject.Money;

import java.time.Instant;
import java.util.UUID;

/**
 * Resultado calculado del reparto de taquilla para un SettlementAgreement. Inmutable en
 * cuanto pasa a APPROVED/PAID (patrón Stripe Charge / Contract.guardEditable): si el
 * cálculo estaba mal, se anula (VOIDED) y se calcula uno nuevo, nunca se parchea uno
 * ya aprobado.
 */
public final class Settlement {

    private final SettlementId           id;
    private final SettlementAgreementId  settlementAgreementId;
    private final UUID                   linkedEventId;
    private final PromoterId             promoterId;
    private final Instant                calculatedAt;

    private SettlementStatus     status;
    private SettlementBreakdown  breakdown;
    private Money                artistPayableAmount;
    private Instant              approvedAt;

    private Settlement(SettlementId id, SettlementAgreementId settlementAgreementId, UUID linkedEventId,
                        PromoterId promoterId, SettlementStatus status, SettlementBreakdown breakdown,
                        Money artistPayableAmount, Instant calculatedAt, Instant approvedAt) {
        this.id                    = id;
        this.settlementAgreementId = settlementAgreementId;
        this.linkedEventId         = linkedEventId;
        this.promoterId            = promoterId;
        this.status                = status;
        this.breakdown              = breakdown;
        this.artistPayableAmount   = artistPayableAmount;
        this.calculatedAt          = calculatedAt;
        this.approvedAt            = approvedAt;
    }

    public static Settlement create(SettlementAgreementId settlementAgreementId, UUID linkedEventId,
                                     PromoterId promoterId, SettlementBreakdown breakdown) {
        return new Settlement(SettlementId.generate(), settlementAgreementId, linkedEventId, promoterId,
                SettlementStatus.DRAFT, breakdown, breakdown.finalArtistAmount(), Instant.now(), null);
    }

    public static Settlement reconstitute(SettlementId id, SettlementAgreementId settlementAgreementId,
                                           UUID linkedEventId, PromoterId promoterId, SettlementStatus status,
                                           SettlementBreakdown breakdown, Money artistPayableAmount,
                                           Instant calculatedAt, Instant approvedAt) {
        return new Settlement(id, settlementAgreementId, linkedEventId, promoterId, status,
                breakdown, artistPayableAmount, calculatedAt, approvedAt);
    }

    public void recalculate(SettlementBreakdown newBreakdown) {
        guardEditable();
        this.breakdown            = newBreakdown;
        this.artistPayableAmount  = newBreakdown.finalArtistAmount();
    }

    public void approve() {
        if (status != SettlementStatus.DRAFT) {
            throw new InvalidSettlementStatusTransitionException(
                    "Cannot approve a settlement in status: " + status);
        }
        this.status     = SettlementStatus.APPROVED;
        this.approvedAt = Instant.now();
    }

    public void markPaid() {
        if (status != SettlementStatus.APPROVED) {
            throw new InvalidSettlementStatusTransitionException(
                    "Cannot mark paid a settlement in status: " + status);
        }
        this.status = SettlementStatus.PAID;
    }

    public void voidSettlement() {
        if (status == SettlementStatus.PAID) {
            throw new InvalidSettlementStatusTransitionException("Cannot void a settlement already PAID");
        }
        this.status = SettlementStatus.VOIDED;
    }

    private void guardEditable() {
        if (status != SettlementStatus.DRAFT) {
            throw new InvalidSettlementStatusTransitionException(
                    "Settlement can only be recalculated in DRAFT status");
        }
    }

    public SettlementId          getId()                    { return id; }
    public SettlementAgreementId getSettlementAgreementId()  { return settlementAgreementId; }
    public UUID                  getLinkedEventId()          { return linkedEventId; }
    public PromoterId             getPromoterId()             { return promoterId; }
    public SettlementStatus      getStatus()                 { return status; }
    public SettlementBreakdown   getBreakdown()               { return breakdown; }
    public Money                  getArtistPayableAmount()    { return artistPayableAmount; }
    public Instant                getCalculatedAt()           { return calculatedAt; }
    public Instant                getApprovedAt()             { return approvedAt; }
}

package com.gresk.modules.finance.domain.model;

import com.gresk.modules.finance.domain.exception.InvalidSettlementStatusTransitionException;
import com.gresk.modules.finance.domain.model.valueobject.DealTerms;
import com.gresk.modules.promoter.domain.model.valueobject.PromoterId;
import com.gresk.shared.domain.valueobject.Money;

import java.time.Instant;
import java.util.UUID;

/**
 * Configuración inmutable de una liquidación (equivalente al Order de Pretix / a la
 * configuración de una Connected Account de Stripe): snapshotea los términos del Contract
 * en el momento de su creación, para que un Contract editado después no altere una
 * liquidación ya configurada. Separado de {@code Settlement} (el resultado calculado).
 */
public final class SettlementAgreement {

    private final SettlementAgreementId id;
    private final PromoterId            promoterId;
    private final UUID                  linkedContractId;
    private final UUID                  linkedEventId;
    private final DealTerms             dealTerms;
    private final Money                 contractFeeSnapshot;
    private final String                artistNameSnapshot;
    private final String                artistTaxIdSnapshot;
    private final String                artistCountrySnapshot;
    private final boolean               artistTaxResidentSnapshot;
    private final Instant               snapshottedAt;

    private SettlementAgreementStatus status;

    private SettlementAgreement(SettlementAgreementId id, PromoterId promoterId, UUID linkedContractId,
                                 UUID linkedEventId, DealTerms dealTerms, Money contractFeeSnapshot,
                                 String artistNameSnapshot, String artistTaxIdSnapshot,
                                 String artistCountrySnapshot, boolean artistTaxResidentSnapshot,
                                 Instant snapshottedAt, SettlementAgreementStatus status) {
        this.id                        = id;
        this.promoterId                = promoterId;
        this.linkedContractId          = linkedContractId;
        this.linkedEventId             = linkedEventId;
        this.dealTerms                 = dealTerms;
        this.contractFeeSnapshot       = contractFeeSnapshot;
        this.artistNameSnapshot        = artistNameSnapshot;
        this.artistTaxIdSnapshot       = artistTaxIdSnapshot;
        this.artistCountrySnapshot     = artistCountrySnapshot;
        this.artistTaxResidentSnapshot = artistTaxResidentSnapshot;
        this.snapshottedAt             = snapshottedAt;
        this.status                    = status;
    }

    public static SettlementAgreement createFromContractSnapshot(
            PromoterId promoterId, UUID linkedContractId, UUID linkedEventId, DealTerms dealTerms,
            Money contractFeeSnapshot, String artistNameSnapshot, String artistTaxIdSnapshot,
            String artistCountrySnapshot, boolean artistTaxResidentSnapshot) {
        return new SettlementAgreement(
                SettlementAgreementId.generate(), promoterId, linkedContractId, linkedEventId, dealTerms,
                contractFeeSnapshot, artistNameSnapshot, artistTaxIdSnapshot, artistCountrySnapshot,
                artistTaxResidentSnapshot, Instant.now(), SettlementAgreementStatus.ACTIVE);
    }

    public static SettlementAgreement reconstitute(
            SettlementAgreementId id, PromoterId promoterId, UUID linkedContractId, UUID linkedEventId,
            DealTerms dealTerms, Money contractFeeSnapshot, String artistNameSnapshot, String artistTaxIdSnapshot,
            String artistCountrySnapshot, boolean artistTaxResidentSnapshot, Instant snapshottedAt,
            SettlementAgreementStatus status) {
        return new SettlementAgreement(id, promoterId, linkedContractId, linkedEventId, dealTerms,
                contractFeeSnapshot, artistNameSnapshot, artistTaxIdSnapshot, artistCountrySnapshot,
                artistTaxResidentSnapshot, snapshottedAt, status);
    }

    /** El caso de uso debe comprobar antes que no existe ningún Settlement APPROVED/PAID asociado. */
    public void voidAgreement() {
        if (status != SettlementAgreementStatus.ACTIVE) {
            throw new InvalidSettlementStatusTransitionException(
                    "Cannot void a settlement agreement in status: " + status);
        }
        this.status = SettlementAgreementStatus.VOIDED;
    }

    public SettlementAgreementId     getId()                        { return id; }
    public PromoterId                getPromoterId()                { return promoterId; }
    public UUID                      getLinkedContractId()          { return linkedContractId; }
    public UUID                      getLinkedEventId()             { return linkedEventId; }
    public DealTerms                 getDealTerms()                 { return dealTerms; }
    public Money                     getContractFeeSnapshot()       { return contractFeeSnapshot; }
    public String                    getArtistNameSnapshot()        { return artistNameSnapshot; }
    public String                    getArtistTaxIdSnapshot()       { return artistTaxIdSnapshot; }
    public String                    getArtistCountrySnapshot()     { return artistCountrySnapshot; }
    public boolean                   isArtistTaxResidentSnapshot()  { return artistTaxResidentSnapshot; }
    public Instant                   getSnapshottedAt()             { return snapshottedAt; }
    public SettlementAgreementStatus getStatus()                    { return status; }
}

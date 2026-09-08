package com.gresk.modules.finance.infrastructure.persistence.mapper;

import com.gresk.modules.finance.domain.model.SettlementAgreement;
import com.gresk.modules.finance.domain.model.SettlementAgreementId;
import com.gresk.modules.finance.domain.model.SettlementAgreementStatus;
import com.gresk.modules.finance.domain.model.valueobject.DealTerms;
import com.gresk.modules.finance.domain.model.valueobject.SettlementDealType;
import com.gresk.modules.finance.infrastructure.persistence.entity.SettlementAgreementEntity;
import com.gresk.modules.promoter.domain.model.valueobject.PromoterId;
import com.gresk.shared.domain.valueobject.Money;
import org.springframework.stereotype.Component;

@Component
public class SettlementAgreementMapper {

    public SettlementAgreement toDomain(SettlementAgreementEntity e) {
        SettlementDealType type = SettlementDealType.valueOf(e.getDealType());
        DealTerms dealTerms = new DealTerms(
                type,
                e.getGuaranteedAmount() != null ? new Money(e.getGuaranteedAmount(), e.getCurrency()) : null,
                e.getArtistPercentage(),
                e.getRevenueThreshold() != null ? new Money(e.getRevenueThreshold(), e.getCurrency()) : null);

        return SettlementAgreement.reconstitute(
                SettlementAgreementId.of(e.getId()),
                PromoterId.of(e.getPromoterId()),
                e.getLinkedContractId(),
                e.getLinkedEventId(),
                dealTerms,
                new Money(e.getContractFeeSnapshot(), e.getCurrency()),
                e.getArtistNameSnapshot(),
                e.getArtistTaxIdSnapshot(),
                e.getArtistCountrySnapshot(),
                e.isArtistTaxResidentSnapshot(),
                e.getSnapshottedAt(),
                SettlementAgreementStatus.valueOf(e.getStatus()));
    }

    public SettlementAgreementEntity toEntity(SettlementAgreement a) {
        DealTerms terms = a.getDealTerms();
        return SettlementAgreementEntity.builder()
                .id(a.getId().value())
                .promoterId(a.getPromoterId().value())
                .linkedContractId(a.getLinkedContractId())
                .linkedEventId(a.getLinkedEventId())
                .dealType(terms.type().name())
                .guaranteedAmount(terms.guaranteedAmount() != null ? terms.guaranteedAmount().amount() : null)
                .artistPercentage(terms.artistPercentage())
                .revenueThreshold(terms.revenueThreshold() != null ? terms.revenueThreshold().amount() : null)
                .currency(a.getContractFeeSnapshot().currency())
                .contractFeeSnapshot(a.getContractFeeSnapshot().amount())
                .artistNameSnapshot(a.getArtistNameSnapshot())
                .artistTaxIdSnapshot(a.getArtistTaxIdSnapshot())
                .artistCountrySnapshot(a.getArtistCountrySnapshot())
                .artistTaxResidentSnapshot(a.isArtistTaxResidentSnapshot())
                .snapshottedAt(a.getSnapshottedAt())
                .status(a.getStatus().name())
                .build();
    }
}

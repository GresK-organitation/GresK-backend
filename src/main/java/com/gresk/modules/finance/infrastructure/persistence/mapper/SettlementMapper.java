package com.gresk.modules.finance.infrastructure.persistence.mapper;

import com.gresk.modules.finance.domain.model.Settlement;
import com.gresk.modules.finance.domain.model.SettlementAgreementId;
import com.gresk.modules.finance.domain.model.SettlementId;
import com.gresk.modules.finance.domain.model.SettlementStatus;
import com.gresk.modules.finance.domain.model.valueobject.SettlementBreakdown;
import com.gresk.modules.finance.domain.model.valueobject.SettlementDealType;
import com.gresk.modules.finance.infrastructure.persistence.entity.SettlementEntity;
import com.gresk.modules.promoter.domain.model.valueobject.PromoterId;
import com.gresk.shared.domain.valueobject.Money;
import org.springframework.stereotype.Component;

@Component
public class SettlementMapper {

    public Settlement toDomain(SettlementEntity e) {
        String currency = e.getCurrency();
        SettlementBreakdown breakdown = new SettlementBreakdown(
                SettlementDealType.valueOf(e.getDealType()),
                new Money(e.getGrossBoxOffice(), currency),
                new Money(e.getNetBoxOffice(), currency),
                e.getTicketingCommissionDeducted() != null ? new Money(e.getTicketingCommissionDeducted(), currency) : null,
                e.getGuaranteedComponent() != null ? new Money(e.getGuaranteedComponent(), currency) : null,
                e.getPercentageComponent() != null ? new Money(e.getPercentageComponent(), currency) : null,
                new Money(e.getArtistPayableAmount(), currency));

        return Settlement.reconstitute(
                SettlementId.of(e.getId()),
                SettlementAgreementId.of(e.getSettlementAgreementId()),
                e.getLinkedEventId(),
                PromoterId.of(e.getPromoterId()),
                SettlementStatus.valueOf(e.getStatus()),
                breakdown,
                new Money(e.getArtistPayableAmount(), currency),
                e.getCalculatedAt(),
                e.getApprovedAt());
    }

    public SettlementEntity toEntity(Settlement s) {
        SettlementBreakdown b = s.getBreakdown();
        return SettlementEntity.builder()
                .id(s.getId().value())
                .settlementAgreementId(s.getSettlementAgreementId().value())
                .linkedEventId(s.getLinkedEventId())
                .promoterId(s.getPromoterId().value())
                .status(s.getStatus().name())
                .dealType(b.dealType().name())
                .currency(s.getArtistPayableAmount().currency())
                .grossBoxOffice(b.grossBoxOffice().amount())
                .netBoxOffice(b.netBoxOffice().amount())
                .ticketingCommissionDeducted(b.ticketingCommissionDeducted() != null ? b.ticketingCommissionDeducted().amount() : null)
                .guaranteedComponent(b.guaranteedComponent() != null ? b.guaranteedComponent().amount() : null)
                .percentageComponent(b.percentageComponent() != null ? b.percentageComponent().amount() : null)
                .artistPayableAmount(s.getArtistPayableAmount().amount())
                .calculatedAt(s.getCalculatedAt())
                .approvedAt(s.getApprovedAt())
                .build();
    }
}

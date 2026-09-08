package com.gresk.modules.finance.application.usecase;

import com.gresk.modules.finance.application.command.CalculateSettlementCommand;
import com.gresk.modules.finance.domain.exception.FinanceResourceNotOwnedException;
import com.gresk.modules.finance.domain.exception.InvalidSettlementStatusTransitionException;
import com.gresk.modules.finance.domain.exception.SettlementAgreementNotFoundException;
import com.gresk.modules.finance.domain.model.Settlement;
import com.gresk.modules.finance.domain.model.SettlementAgreement;
import com.gresk.modules.finance.domain.model.SettlementAgreementId;
import com.gresk.modules.finance.domain.model.SettlementAgreementStatus;
import com.gresk.modules.finance.domain.model.valueobject.CostSubcategory;
import com.gresk.modules.finance.domain.model.valueobject.EventRevenueSnapshot;
import com.gresk.modules.finance.domain.model.valueobject.SettlementBreakdown;
import com.gresk.modules.finance.domain.port.out.EventFinancialPlanRepositoryPort;
import com.gresk.modules.finance.domain.port.out.EventRevenueProviderPort;
import com.gresk.modules.finance.domain.port.out.SettlementAgreementRepositoryPort;
import com.gresk.modules.finance.domain.port.out.SettlementRepositoryPort;
import com.gresk.modules.finance.domain.service.SettlementCalculator;
import com.gresk.modules.promoter.domain.model.valueobject.PromoterId;
import com.gresk.shared.domain.valueobject.Money;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class CalculateSettlementUseCase {

    private final SettlementAgreementRepositoryPort agreementRepository;
    private final SettlementRepositoryPort          settlementRepository;
    private final EventRevenueProviderPort          revenueProvider;
    private final EventFinancialPlanRepositoryPort  planRepository;

    public Settlement execute(CalculateSettlementCommand cmd) {
        SettlementAgreementId agreementId = SettlementAgreementId.of(cmd.agreementId());
        SettlementAgreement agreement = agreementRepository.findById(agreementId)
                .orElseThrow(() -> new SettlementAgreementNotFoundException(cmd.agreementId()));

        if (!agreement.getPromoterId().equals(PromoterId.of(cmd.promoterId()))) {
            throw new FinanceResourceNotOwnedException();
        }
        if (agreement.getStatus() != SettlementAgreementStatus.ACTIVE) {
            throw new InvalidSettlementStatusTransitionException(
                    "Cannot calculate a settlement for a voided agreement");
        }

        EventRevenueSnapshot revenue = revenueProvider.getRevenueSnapshot(agreement.getLinkedEventId());
        Money ticketingCommission = resolveTicketingCommission(agreement, revenue.grossBoxOffice());

        SettlementBreakdown breakdown = SettlementCalculator.calculate(
                agreement.getDealTerms(), revenue.grossBoxOffice(), ticketingCommission);

        List<Settlement> existing = settlementRepository.findBySettlementAgreementId(agreementId);
        Settlement settlement = existing.stream()
                .max(Comparator.comparing(Settlement::getCalculatedAt))
                .orElse(null);

        if (settlement == null) {
            settlement = Settlement.create(agreementId, agreement.getLinkedEventId(), agreement.getPromoterId(), breakdown);
        } else {
            settlement.recalculate(breakdown);
        }

        return settlementRepository.save(settlement);
    }

    private Money resolveTicketingCommission(SettlementAgreement agreement, Money grossBoxOffice) {
        return planRepository.findByLinkedEventId(agreement.getLinkedEventId())
                .map(plan -> plan.getCostLines().stream()
                        .filter(line -> line.subcategory() == CostSubcategory.TICKETING_COMMISSION)
                        .map(line -> line.resolveAmount(grossBoxOffice))
                        .reduce(Money.zero(grossBoxOffice.currency()), Money::add))
                .orElse(Money.zero(grossBoxOffice.currency()));
    }
}

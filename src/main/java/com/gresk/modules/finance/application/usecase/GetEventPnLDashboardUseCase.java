package com.gresk.modules.finance.application.usecase;

import com.gresk.modules.finance.application.query.GetEventPnLDashboardQuery;
import com.gresk.modules.finance.domain.exception.EventFinancialPlanNotFoundException;
import com.gresk.modules.finance.domain.exception.FinanceResourceNotOwnedException;
import com.gresk.modules.finance.domain.model.EventFinancialPlan;
import com.gresk.modules.finance.domain.model.valueobject.EventPnLDashboard;
import com.gresk.modules.finance.domain.model.valueobject.EventRevenueSnapshot;
import com.gresk.modules.finance.domain.port.out.EventFinancialPlanRepositoryPort;
import com.gresk.modules.finance.domain.port.out.EventRevenueProviderPort;
import com.gresk.modules.finance.domain.service.ProfitAndLossCalculator;
import com.gresk.modules.promoter.domain.model.valueobject.PromoterId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GetEventPnLDashboardUseCase {

    private final EventFinancialPlanRepositoryPort planRepository;
    private final EventRevenueProviderPort          revenueProvider;

    public EventPnLDashboard execute(GetEventPnLDashboardQuery query) {
        UUID linkedEventId = UUID.fromString(query.linkedEventId());
        EventFinancialPlan plan = planRepository.findByLinkedEventId(linkedEventId)
                .orElseThrow(() -> new EventFinancialPlanNotFoundException(query.linkedEventId()));

        if (!plan.getPromoterId().equals(PromoterId.of(query.promoterId()))) {
            throw new FinanceResourceNotOwnedException();
        }

        EventRevenueSnapshot revenue = revenueProvider.getRevenueSnapshot(linkedEventId);
        return ProfitAndLossCalculator.calculate(plan, revenue);
    }
}

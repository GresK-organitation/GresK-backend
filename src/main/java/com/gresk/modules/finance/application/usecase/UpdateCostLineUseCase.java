package com.gresk.modules.finance.application.usecase;

import com.gresk.modules.finance.application.command.UpdateCostLineCommand;
import com.gresk.modules.finance.domain.exception.EventFinancialPlanNotFoundException;
import com.gresk.modules.finance.domain.exception.FinanceResourceNotOwnedException;
import com.gresk.modules.finance.domain.model.EventFinancialPlan;
import com.gresk.modules.finance.domain.model.EventFinancialPlanId;
import com.gresk.modules.finance.domain.model.valueobject.CostLine;
import com.gresk.modules.finance.domain.model.valueobject.CostLineId;
import com.gresk.modules.finance.domain.port.out.EventFinancialPlanRepositoryPort;
import com.gresk.modules.promoter.domain.model.valueobject.PromoterId;
import com.gresk.shared.domain.valueobject.Money;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class UpdateCostLineUseCase {

    private final EventFinancialPlanRepositoryPort planRepository;

    public EventFinancialPlan execute(UpdateCostLineCommand cmd) {
        EventFinancialPlan plan = planRepository.findById(EventFinancialPlanId.of(cmd.planId()))
                .orElseThrow(() -> new EventFinancialPlanNotFoundException(cmd.planId()));

        if (!plan.getPromoterId().equals(PromoterId.of(cmd.promoterId()))) {
            throw new FinanceResourceNotOwnedException();
        }

        Money budgetedAmount = cmd.budgetedAmount() != null
                ? new Money(cmd.budgetedAmount(), cmd.currency() != null ? cmd.currency() : "EUR")
                : null;

        CostLineId costLineId = CostLineId.of(cmd.costLineId());
        CostLine updated = new CostLine(
                costLineId, cmd.category(), cmd.subcategory(),
                cmd.description(), budgetedAmount, cmd.variablePercentage());

        plan.updateCostLine(costLineId, updated);
        return planRepository.save(plan);
    }
}

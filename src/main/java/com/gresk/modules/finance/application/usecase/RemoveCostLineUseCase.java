package com.gresk.modules.finance.application.usecase;

import com.gresk.modules.finance.application.command.RemoveCostLineCommand;
import com.gresk.modules.finance.domain.exception.EventFinancialPlanNotFoundException;
import com.gresk.modules.finance.domain.exception.FinanceResourceNotOwnedException;
import com.gresk.modules.finance.domain.model.EventFinancialPlan;
import com.gresk.modules.finance.domain.model.EventFinancialPlanId;
import com.gresk.modules.finance.domain.model.valueobject.CostLineId;
import com.gresk.modules.finance.domain.port.out.EventFinancialPlanRepositoryPort;
import com.gresk.modules.promoter.domain.model.valueobject.PromoterId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class RemoveCostLineUseCase {

    private final EventFinancialPlanRepositoryPort planRepository;

    public void execute(RemoveCostLineCommand cmd) {
        EventFinancialPlan plan = planRepository.findById(EventFinancialPlanId.of(cmd.planId()))
                .orElseThrow(() -> new EventFinancialPlanNotFoundException(cmd.planId()));

        if (!plan.getPromoterId().equals(PromoterId.of(cmd.promoterId()))) {
            throw new FinanceResourceNotOwnedException();
        }

        plan.removeCostLine(CostLineId.of(cmd.costLineId()));
        planRepository.save(plan);
    }
}

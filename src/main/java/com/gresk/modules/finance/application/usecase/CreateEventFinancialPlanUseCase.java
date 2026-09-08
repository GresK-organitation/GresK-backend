package com.gresk.modules.finance.application.usecase;

import com.gresk.modules.finance.application.command.CreateEventFinancialPlanCommand;
import com.gresk.modules.finance.domain.exception.EventFinancialPlanAlreadyExistsException;
import com.gresk.modules.finance.domain.model.EventFinancialPlan;
import com.gresk.modules.finance.domain.port.out.EventFinancialPlanRepositoryPort;
import com.gresk.modules.promoter.domain.model.valueobject.PromoterId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class CreateEventFinancialPlanUseCase {

    private final EventFinancialPlanRepositoryPort planRepository;

    public EventFinancialPlan execute(CreateEventFinancialPlanCommand cmd) {
        UUID linkedEventId = UUID.fromString(cmd.linkedEventId());
        if (planRepository.existsByLinkedEventId(linkedEventId)) {
            throw new EventFinancialPlanAlreadyExistsException(cmd.linkedEventId());
        }

        EventFinancialPlan plan = EventFinancialPlan.create(
                PromoterId.of(cmd.promoterId()), linkedEventId, cmd.deviationThresholdPercentage());
        return planRepository.save(plan);
    }
}

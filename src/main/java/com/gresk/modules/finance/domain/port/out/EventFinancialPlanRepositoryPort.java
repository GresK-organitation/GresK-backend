package com.gresk.modules.finance.domain.port.out;

import com.gresk.modules.finance.domain.model.EventFinancialPlan;
import com.gresk.modules.finance.domain.model.EventFinancialPlanId;

import java.util.Optional;
import java.util.UUID;

public interface EventFinancialPlanRepositoryPort {
    EventFinancialPlan save(EventFinancialPlan plan);
    Optional<EventFinancialPlan> findById(EventFinancialPlanId id);
    Optional<EventFinancialPlan> findByLinkedEventId(UUID linkedEventId);
    boolean existsByLinkedEventId(UUID linkedEventId);
}

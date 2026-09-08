package com.gresk.modules.finance.infrastructure.persistence.adapter;

import com.gresk.modules.finance.domain.model.EventFinancialPlan;
import com.gresk.modules.finance.domain.model.EventFinancialPlanId;
import com.gresk.modules.finance.domain.port.out.EventFinancialPlanRepositoryPort;
import com.gresk.modules.finance.infrastructure.persistence.entity.EventFinancialPlanEntity;
import com.gresk.modules.finance.infrastructure.persistence.mapper.EventFinancialPlanMapper;
import com.gresk.modules.finance.infrastructure.persistence.repository.CostLineJpaRepository;
import com.gresk.modules.finance.infrastructure.persistence.repository.EventFinancialPlanJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class JpaEventFinancialPlanRepositoryAdapter implements EventFinancialPlanRepositoryPort {

    private final EventFinancialPlanJpaRepository planRepo;
    private final CostLineJpaRepository            costLineRepo;
    private final EventFinancialPlanMapper         mapper;

    @Override
    @Transactional
    public EventFinancialPlan save(EventFinancialPlan plan) {
        EventFinancialPlanEntity savedPlan = planRepo.save(mapper.toEntity(plan));
        costLineRepo.deleteByPlanId(savedPlan.getId());
        costLineRepo.flush();
        var savedCostLines = costLineRepo.saveAll(mapper.toEntities(plan));
        return mapper.toDomain(savedPlan, savedCostLines);
    }

    @Override
    public Optional<EventFinancialPlan> findById(EventFinancialPlanId id) {
        return planRepo.findById(id.value())
                .map(e -> mapper.toDomain(e, costLineRepo.findByPlanId(e.getId())));
    }

    @Override
    public Optional<EventFinancialPlan> findByLinkedEventId(UUID linkedEventId) {
        return planRepo.findByLinkedEventId(linkedEventId)
                .map(e -> mapper.toDomain(e, costLineRepo.findByPlanId(e.getId())));
    }

    @Override
    public boolean existsByLinkedEventId(UUID linkedEventId) {
        return planRepo.existsByLinkedEventId(linkedEventId);
    }
}

package com.gresk.modules.finance.infrastructure.persistence.repository;

import com.gresk.modules.finance.infrastructure.persistence.entity.EventFinancialPlanEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface EventFinancialPlanJpaRepository extends JpaRepository<EventFinancialPlanEntity, UUID> {
    Optional<EventFinancialPlanEntity> findByLinkedEventId(UUID linkedEventId);
    boolean existsByLinkedEventId(UUID linkedEventId);
}

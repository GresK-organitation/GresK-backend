package com.gresk.modules.finance.infrastructure.persistence.repository;

import com.gresk.modules.finance.infrastructure.persistence.entity.CostLineEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface CostLineJpaRepository extends JpaRepository<CostLineEntity, UUID> {
    List<CostLineEntity> findByPlanId(UUID planId);
    void deleteByPlanId(UUID planId);
}

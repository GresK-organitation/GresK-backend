package com.gresk.modules.finance.infrastructure.persistence.repository;

import com.gresk.modules.finance.infrastructure.persistence.entity.SettlementEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface SettlementJpaRepository extends JpaRepository<SettlementEntity, UUID> {
    List<SettlementEntity> findBySettlementAgreementId(UUID settlementAgreementId);
    List<SettlementEntity> findByPromoterId(UUID promoterId);
}

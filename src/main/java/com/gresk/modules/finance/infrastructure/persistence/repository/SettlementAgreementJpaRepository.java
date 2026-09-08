package com.gresk.modules.finance.infrastructure.persistence.repository;

import com.gresk.modules.finance.infrastructure.persistence.entity.SettlementAgreementEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SettlementAgreementJpaRepository extends JpaRepository<SettlementAgreementEntity, UUID> {
    Optional<SettlementAgreementEntity> findByLinkedContractIdAndStatus(UUID linkedContractId, String status);
    List<SettlementAgreementEntity> findByPromoterId(UUID promoterId);
}

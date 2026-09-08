package com.gresk.modules.contract.infrastructure.persistence.repository;

import com.gresk.modules.contract.infrastructure.persistence.entity.ContractAuditTrailEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ContractAuditTrailJpaRepository extends JpaRepository<ContractAuditTrailEntity, UUID> {
    List<ContractAuditTrailEntity> findByContractIdOrderByOccurredAtAsc(UUID contractId);
}

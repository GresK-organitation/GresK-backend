package com.gresk.modules.contract.infrastructure.persistence.repository;

import com.gresk.modules.contract.domain.model.ContractVersionStatus;
import com.gresk.modules.contract.infrastructure.persistence.entity.ContractVersionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ContractVersionJpaRepository extends JpaRepository<ContractVersionEntity, UUID> {
    List<ContractVersionEntity> findByContractIdOrderByVersionNumberDesc(UUID contractId);
    Optional<ContractVersionEntity> findByContractIdAndStatus(UUID contractId, ContractVersionStatus status);
}

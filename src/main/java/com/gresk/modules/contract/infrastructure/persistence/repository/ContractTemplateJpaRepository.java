package com.gresk.modules.contract.infrastructure.persistence.repository;

import com.gresk.modules.contract.domain.model.ContractType;
import com.gresk.modules.contract.infrastructure.persistence.entity.ContractTemplateEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ContractTemplateJpaRepository extends JpaRepository<ContractTemplateEntity, UUID> {
    Optional<ContractTemplateEntity> findByPromoterIdIsNullAndTypeAndActiveTrue(ContractType type);
    List<ContractTemplateEntity> findByPromoterId(UUID promoterId);
}

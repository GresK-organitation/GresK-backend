package com.gresk.modules.contract.infrastructure.persistence.adapter;

import com.gresk.modules.contract.domain.model.ContractTemplate;
import com.gresk.modules.contract.domain.model.ContractTemplateId;
import com.gresk.modules.contract.domain.model.ContractType;
import com.gresk.modules.contract.domain.port.out.ContractTemplateRepositoryPort;
import com.gresk.modules.contract.infrastructure.persistence.mapper.ContractTemplateMapper;
import com.gresk.modules.contract.infrastructure.persistence.repository.ContractTemplateJpaRepository;
import com.gresk.modules.promoter.domain.model.valueobject.PromoterId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class JpaContractTemplateRepositoryAdapter implements ContractTemplateRepositoryPort {

    private final ContractTemplateJpaRepository repo;
    private final ContractTemplateMapper        mapper;

    @Override
    @Transactional
    public ContractTemplate save(ContractTemplate template) {
        return mapper.toDomain(repo.save(mapper.toEntity(template)));
    }

    @Override
    public Optional<ContractTemplate> findById(ContractTemplateId id) {
        return repo.findById(id.value()).map(mapper::toDomain);
    }

    @Override
    public Optional<ContractTemplate> findSystemDefaultByType(ContractType type) {
        return repo.findByPromoterIdIsNullAndTypeAndActiveTrue(type).map(mapper::toDomain);
    }

    @Override
    public List<ContractTemplate> findAllByPromoterId(PromoterId promoterId) {
        return repo.findByPromoterId(promoterId.value()).stream().map(mapper::toDomain).toList();
    }
}

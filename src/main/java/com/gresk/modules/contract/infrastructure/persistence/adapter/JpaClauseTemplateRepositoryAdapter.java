package com.gresk.modules.contract.infrastructure.persistence.adapter;

import com.gresk.modules.contract.domain.model.ClauseTemplate;
import com.gresk.modules.contract.domain.model.ClauseTemplateId;
import com.gresk.modules.contract.domain.model.ContractType;
import com.gresk.modules.contract.domain.port.out.ClauseTemplateRepositoryPort;
import com.gresk.modules.contract.infrastructure.persistence.mapper.ClauseTemplateMapper;
import com.gresk.modules.contract.infrastructure.persistence.repository.ClauseTemplateJpaRepository;
import com.gresk.modules.promoter.domain.model.valueobject.PromoterId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class JpaClauseTemplateRepositoryAdapter implements ClauseTemplateRepositoryPort {

    private final ClauseTemplateJpaRepository repo;
    private final ClauseTemplateMapper        mapper;

    @Override
    @Transactional
    public ClauseTemplate save(ClauseTemplate template) {
        return mapper.toDomain(repo.save(mapper.toEntity(template)));
    }

    @Override
    public Optional<ClauseTemplate> findById(ClauseTemplateId id) {
        return repo.findById(id.value()).map(mapper::toDomain);
    }

    @Override
    public List<ClauseTemplate> findSystemDefaultsByType(ContractType type) {
        return repo.findSystemDefaultsByType(type.name()).stream().map(mapper::toDomain).toList();
    }

    @Override
    public List<ClauseTemplate> findByPromoterId(PromoterId promoterId) {
        return repo.findByPromoterId(promoterId.value()).stream().map(mapper::toDomain).toList();
    }
}

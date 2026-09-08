package com.gresk.modules.contract.infrastructure.persistence.adapter;

import com.gresk.modules.contract.domain.model.ContractId;
import com.gresk.modules.contract.domain.model.ContractVersion;
import com.gresk.modules.contract.domain.model.ContractVersionId;
import com.gresk.modules.contract.domain.model.ContractVersionStatus;
import com.gresk.modules.contract.domain.port.out.ContractVersionRepositoryPort;
import com.gresk.modules.contract.infrastructure.persistence.mapper.ContractVersionMapper;
import com.gresk.modules.contract.infrastructure.persistence.repository.ContractVersionJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class JpaContractVersionRepositoryAdapter implements ContractVersionRepositoryPort {

    private final ContractVersionJpaRepository repo;
    private final ContractVersionMapper        mapper;

    @Override
    @Transactional
    public ContractVersion save(ContractVersion version) {
        return mapper.toDomain(repo.save(mapper.toEntity(version)));
    }

    @Override
    public List<ContractVersion> findByContractId(ContractId contractId) {
        return repo.findByContractIdOrderByVersionNumberDesc(contractId.value()).stream()
                .map(mapper::toDomain).toList();
    }

    @Override
    public Optional<ContractVersion> findCurrent(ContractId contractId) {
        return repo.findByContractIdAndStatus(contractId.value(), ContractVersionStatus.CURRENT).map(mapper::toDomain);
    }

    @Override
    public Optional<ContractVersion> findById(ContractVersionId id) {
        return repo.findById(id.value()).map(mapper::toDomain);
    }
}

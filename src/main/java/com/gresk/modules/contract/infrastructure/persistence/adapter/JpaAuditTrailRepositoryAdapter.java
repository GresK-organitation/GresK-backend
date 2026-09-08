package com.gresk.modules.contract.infrastructure.persistence.adapter;

import com.gresk.modules.contract.domain.model.AuditTrailEntry;
import com.gresk.modules.contract.domain.model.ContractId;
import com.gresk.modules.contract.domain.port.out.AuditTrailRepositoryPort;
import com.gresk.modules.contract.infrastructure.persistence.mapper.ContractAuditTrailMapper;
import com.gresk.modules.contract.infrastructure.persistence.repository.ContractAuditTrailJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class JpaAuditTrailRepositoryAdapter implements AuditTrailRepositoryPort {

    private final ContractAuditTrailJpaRepository repo;
    private final ContractAuditTrailMapper         mapper;

    @Override
    @Transactional
    public void append(AuditTrailEntry entry) {
        repo.save(mapper.toEntity(entry));
    }

    @Override
    public List<AuditTrailEntry> findByContractId(ContractId contractId) {
        return repo.findByContractIdOrderByOccurredAtAsc(contractId.value()).stream()
                .map(mapper::toDomain).toList();
    }
}

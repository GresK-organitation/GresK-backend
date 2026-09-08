package com.gresk.modules.contract.infrastructure.persistence.adapter;

import com.gresk.modules.contract.domain.model.ContractId;
import com.gresk.modules.contract.domain.model.SignatureEnvelope;
import com.gresk.modules.contract.domain.model.SignatureEnvelopeId;
import com.gresk.modules.contract.domain.port.out.SignatureEnvelopeRepositoryPort;
import com.gresk.modules.contract.infrastructure.persistence.mapper.SignatureEnvelopeMapper;
import com.gresk.modules.contract.infrastructure.persistence.repository.SignatureEnvelopeJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class JpaSignatureEnvelopeRepositoryAdapter implements SignatureEnvelopeRepositoryPort {

    private final SignatureEnvelopeJpaRepository repo;
    private final SignatureEnvelopeMapper        mapper;

    @Override
    @Transactional
    public SignatureEnvelope save(SignatureEnvelope envelope) {
        return mapper.toDomain(repo.save(mapper.toEntity(envelope)));
    }

    @Override
    public Optional<SignatureEnvelope> findById(SignatureEnvelopeId id) {
        return repo.findById(id.value()).map(mapper::toDomain);
    }

    @Override
    public List<SignatureEnvelope> findByContractId(ContractId contractId) {
        return repo.findByContractId(contractId.value()).stream().map(mapper::toDomain).toList();
    }

    @Override
    public Optional<SignatureEnvelope> findByProviderEnvelopeId(String providerEnvelopeId) {
        return repo.findByProviderEnvelopeId(providerEnvelopeId).map(mapper::toDomain);
    }
}

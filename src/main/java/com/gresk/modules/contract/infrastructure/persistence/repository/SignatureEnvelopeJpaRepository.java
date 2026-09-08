package com.gresk.modules.contract.infrastructure.persistence.repository;

import com.gresk.modules.contract.infrastructure.persistence.entity.SignatureEnvelopeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SignatureEnvelopeJpaRepository extends JpaRepository<SignatureEnvelopeEntity, UUID> {
    List<SignatureEnvelopeEntity> findByContractId(UUID contractId);
    Optional<SignatureEnvelopeEntity> findByProviderEnvelopeId(String providerEnvelopeId);
}

package com.gresk.modules.contract.domain.port.out;

import com.gresk.modules.contract.domain.model.ContractId;
import com.gresk.modules.contract.domain.model.SignatureEnvelope;
import com.gresk.modules.contract.domain.model.SignatureEnvelopeId;

import java.util.List;
import java.util.Optional;

public interface SignatureEnvelopeRepositoryPort {
    SignatureEnvelope           save(SignatureEnvelope envelope);
    Optional<SignatureEnvelope> findById(SignatureEnvelopeId id);
    List<SignatureEnvelope>     findByContractId(ContractId contractId);
    Optional<SignatureEnvelope> findByProviderEnvelopeId(String providerEnvelopeId);
}

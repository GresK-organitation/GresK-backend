package com.gresk.modules.contract.application.usecase;

import com.gresk.modules.contract.domain.exception.ContractNotFoundException;
import com.gresk.modules.contract.domain.exception.ContractNotOwnedException;
import com.gresk.modules.contract.domain.exception.SignatureEnvelopeNotFoundException;
import com.gresk.modules.contract.domain.model.Contract;
import com.gresk.modules.contract.domain.model.ContractId;
import com.gresk.modules.contract.domain.model.SignatureEnvelope;
import com.gresk.modules.contract.domain.model.SignatureEnvelopeId;
import com.gresk.modules.contract.domain.port.out.ContractRepositoryPort;
import com.gresk.modules.contract.domain.port.out.SignatureEnvelopeRepositoryPort;
import com.gresk.modules.promoter.domain.model.valueobject.PromoterId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GetSignatureEnvelopeUseCase {

    private final ContractRepositoryPort          contractRepository;
    private final SignatureEnvelopeRepositoryPort envelopeRepository;

    public SignatureEnvelope execute(String contractId, String envelopeId, String promoterId) {
        Contract contract = contractRepository.findById(ContractId.of(contractId))
                .orElseThrow(() -> new ContractNotFoundException(contractId));
        if (!contract.getPromoterId().equals(PromoterId.of(promoterId))) {
            throw new ContractNotOwnedException();
        }
        return envelopeRepository.findById(SignatureEnvelopeId.of(envelopeId))
                .filter(e -> e.getContractId().equals(contract.getId()))
                .orElseThrow(() -> new SignatureEnvelopeNotFoundException(envelopeId));
    }
}

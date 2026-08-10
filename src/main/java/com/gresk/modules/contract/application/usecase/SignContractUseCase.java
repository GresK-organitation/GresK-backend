package com.gresk.modules.contract.application.usecase;

import com.gresk.modules.contract.domain.exception.ContractNotFoundException;
import com.gresk.modules.contract.domain.exception.ContractNotOwnedException;
import com.gresk.modules.contract.domain.model.Contract;
import com.gresk.modules.contract.domain.model.ContractId;
import com.gresk.modules.contract.domain.port.out.ContractRepositoryPort;
import com.gresk.modules.contract.infrastructure.event.ContractSignedEvent;
import com.gresk.modules.promoter.domain.model.valueobject.PromoterId;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class SignContractUseCase {

    private final ContractRepositoryPort   contractRepository;
    private final ApplicationEventPublisher eventPublisher;

    public Contract execute(String contractId, String promoterId) {
        Contract contract = contractRepository.findById(ContractId.of(contractId))
                .orElseThrow(() -> new ContractNotFoundException(contractId));
        if (!contract.getPromoterId().equals(PromoterId.of(promoterId))) {
            throw new ContractNotOwnedException();
        }
        contract.sign();
        Contract saved = contractRepository.save(contract);

        eventPublisher.publishEvent(new ContractSignedEvent(
                this,
                saved.getId(),
                saved.getPromoterId(),
                saved.getType(),
                saved.getLinkedEventId(),
                saved.getReferenceNumber(),
                saved.getPartyB() != null ? saved.getPartyB().name() : null,
                saved.getFeeAmount()
        ));

        return saved;
    }
}

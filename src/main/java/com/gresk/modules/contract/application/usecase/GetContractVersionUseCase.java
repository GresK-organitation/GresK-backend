package com.gresk.modules.contract.application.usecase;

import com.gresk.modules.contract.domain.exception.ContractNotFoundException;
import com.gresk.modules.contract.domain.exception.ContractNotOwnedException;
import com.gresk.modules.contract.domain.exception.ContractVersionNotFoundException;
import com.gresk.modules.contract.domain.model.Contract;
import com.gresk.modules.contract.domain.model.ContractId;
import com.gresk.modules.contract.domain.model.ContractVersion;
import com.gresk.modules.contract.domain.model.ContractVersionId;
import com.gresk.modules.contract.domain.port.out.ContractRepositoryPort;
import com.gresk.modules.contract.domain.port.out.ContractVersionRepositoryPort;
import com.gresk.modules.promoter.domain.model.valueobject.PromoterId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GetContractVersionUseCase {

    private final ContractRepositoryPort        contractRepository;
    private final ContractVersionRepositoryPort  versionRepository;

    public ContractVersion execute(String contractId, String versionId, String promoterId) {
        Contract contract = contractRepository.findById(ContractId.of(contractId))
                .orElseThrow(() -> new ContractNotFoundException(contractId));
        if (!contract.getPromoterId().equals(PromoterId.of(promoterId))) {
            throw new ContractNotOwnedException();
        }
        return versionRepository.findById(ContractVersionId.of(versionId))
                .filter(v -> v.getContractId().equals(contract.getId()))
                .orElseThrow(() -> new ContractVersionNotFoundException(versionId));
    }
}

package com.gresk.modules.contract.application.usecase;

import com.gresk.modules.contract.domain.exception.ContractNotFoundException;
import com.gresk.modules.contract.domain.exception.ContractNotOwnedException;
import com.gresk.modules.contract.domain.model.Contract;
import com.gresk.modules.contract.domain.model.ContractId;
import com.gresk.modules.contract.domain.model.ContractVersion;
import com.gresk.modules.contract.domain.model.valueobject.AuditAction;
import com.gresk.modules.contract.domain.model.AuditTrailEntry;
import com.gresk.modules.contract.domain.port.out.AuditTrailRepositoryPort;
import com.gresk.modules.contract.domain.port.out.ContractRepositoryPort;
import com.gresk.modules.contract.domain.port.out.ContractVersionRepositoryPort;
import com.gresk.modules.promoter.domain.model.valueobject.PromoterId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

/** Snapshot manual, explícito, disparado por la promotora durante la negociación. */
@Service
@RequiredArgsConstructor
@Transactional
public class CreateContractVersionUseCase {

    private final ContractRepositoryPort        contractRepository;
    private final ContractVersionRepositoryPort  versionRepository;
    private final AuditTrailRepositoryPort       auditTrailRepository;

    public ContractVersion execute(String contractId, String promoterId, String changeSummary) {
        Contract contract = contractRepository.findById(ContractId.of(contractId))
                .orElseThrow(() -> new ContractNotFoundException(contractId));
        if (!contract.getPromoterId().equals(PromoterId.of(promoterId))) {
            throw new ContractNotOwnedException();
        }

        versionRepository.findCurrent(contract.getId()).ifPresent(v -> {
            v.markSuperseded();
            versionRepository.save(v);
        });

        ContractVersion version = ContractVersion.snapshot(contract, changeSummary, promoterId);
        ContractVersion saved = versionRepository.save(version);

        contract.advanceVersion();
        contractRepository.save(contract);

        auditTrailRepository.append(AuditTrailEntry.record(contract.getId(), AuditAction.VERSION_CREATED,
                promoterId, null, null, Map.of("versionNumber", String.valueOf(saved.getVersionNumber())), null));

        return saved;
    }
}

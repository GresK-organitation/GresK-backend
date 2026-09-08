package com.gresk.modules.finance.infrastructure.adapter;

import com.gresk.modules.contract.domain.model.Contract;
import com.gresk.modules.contract.domain.model.ContractId;
import com.gresk.modules.contract.domain.port.out.ContractRepositoryPort;
import com.gresk.modules.finance.domain.port.out.ContractSnapshotProviderPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

/** Sin persistencia propia — inyecta directamente el puerto out de contract, mismo patrón que ArtistTourHistoryAdapter. */
@Component
@RequiredArgsConstructor
public class ContractSnapshotProviderAdapter implements ContractSnapshotProviderPort {

    private final ContractRepositoryPort contractRepository;

    @Override
    public Optional<Contract> findById(UUID contractId) {
        return contractRepository.findById(ContractId.of(contractId));
    }
}

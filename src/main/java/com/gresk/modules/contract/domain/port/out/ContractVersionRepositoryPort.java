package com.gresk.modules.contract.domain.port.out;

import com.gresk.modules.contract.domain.model.ContractId;
import com.gresk.modules.contract.domain.model.ContractVersion;
import com.gresk.modules.contract.domain.model.ContractVersionId;

import java.util.List;
import java.util.Optional;

public interface ContractVersionRepositoryPort {
    ContractVersion         save(ContractVersion version);
    List<ContractVersion>   findByContractId(ContractId contractId);
    Optional<ContractVersion> findCurrent(ContractId contractId);
    Optional<ContractVersion> findById(ContractVersionId id);
}

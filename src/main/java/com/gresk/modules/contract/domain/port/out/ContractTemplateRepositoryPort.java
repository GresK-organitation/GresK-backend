package com.gresk.modules.contract.domain.port.out;

import com.gresk.modules.contract.domain.model.ContractTemplate;
import com.gresk.modules.contract.domain.model.ContractTemplateId;
import com.gresk.modules.contract.domain.model.ContractType;
import com.gresk.modules.promoter.domain.model.valueobject.PromoterId;

import java.util.List;
import java.util.Optional;

public interface ContractTemplateRepositoryPort {
    ContractTemplate           save(ContractTemplate template);
    Optional<ContractTemplate> findById(ContractTemplateId id);
    Optional<ContractTemplate> findSystemDefaultByType(ContractType type);
    List<ContractTemplate>     findAllByPromoterId(PromoterId promoterId);
}

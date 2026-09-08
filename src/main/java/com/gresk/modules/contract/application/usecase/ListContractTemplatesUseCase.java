package com.gresk.modules.contract.application.usecase;

import com.gresk.modules.contract.domain.model.ContractTemplate;
import com.gresk.modules.contract.domain.model.ContractType;
import com.gresk.modules.contract.domain.port.out.ContractTemplateRepositoryPort;
import com.gresk.modules.promoter.domain.model.valueobject.PromoterId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ListContractTemplatesUseCase {

    private final ContractTemplateRepositoryPort contractTemplateRepository;

    public List<ContractTemplate> execute(String promoterId, ContractType type) {
        List<ContractTemplate> result = new ArrayList<>();
        if (type != null) {
            contractTemplateRepository.findSystemDefaultByType(type).ifPresent(result::add);
        }
        result.addAll(contractTemplateRepository.findAllByPromoterId(PromoterId.of(promoterId)));
        return result;
    }
}

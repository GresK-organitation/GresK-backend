package com.gresk.modules.contract.application.usecase;

import com.gresk.modules.contract.domain.exception.ContractTemplateNotFoundException;
import com.gresk.modules.contract.domain.model.ContractTemplate;
import com.gresk.modules.contract.domain.model.ContractTemplateId;
import com.gresk.modules.contract.domain.port.out.ContractTemplateRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GetContractTemplateUseCase {

    private final ContractTemplateRepositoryPort contractTemplateRepository;

    public ContractTemplate execute(String templateId) {
        return contractTemplateRepository.findById(ContractTemplateId.of(templateId))
                .orElseThrow(() -> new ContractTemplateNotFoundException(templateId));
    }
}

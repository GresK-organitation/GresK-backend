package com.gresk.modules.contract.application.usecase;

import com.gresk.modules.contract.application.command.CreateContractTemplateCommand;
import com.gresk.modules.contract.domain.exception.ContractTemplateNotFoundException;
import com.gresk.modules.contract.domain.model.ClauseTemplateId;
import com.gresk.modules.contract.domain.model.ContractTemplate;
import com.gresk.modules.contract.domain.model.ContractTemplateId;
import com.gresk.modules.contract.domain.exception.ContractTemplateNotOwnedException;
import com.gresk.modules.contract.domain.port.out.ContractTemplateRepositoryPort;
import com.gresk.modules.promoter.domain.model.valueobject.PromoterId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class UpdateContractTemplateUseCase {

    private final ContractTemplateRepositoryPort contractTemplateRepository;

    public ContractTemplate execute(String templateId, CreateContractTemplateCommand cmd) {
        ContractTemplate template = contractTemplateRepository.findById(ContractTemplateId.of(templateId))
                .orElseThrow(() -> new ContractTemplateNotFoundException(templateId));
        if (!template.isOwnedBy(PromoterId.of(cmd.promoterId()))) {
            throw new ContractTemplateNotOwnedException();
        }
        template.update(cmd.name(), cmd.bodyMarkdown(), cmd.variables(),
                cmd.defaultClauseTemplateIds().stream().map(ClauseTemplateId::of).toList());
        return contractTemplateRepository.save(template);
    }
}

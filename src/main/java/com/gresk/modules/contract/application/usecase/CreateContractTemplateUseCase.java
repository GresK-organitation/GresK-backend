package com.gresk.modules.contract.application.usecase;

import com.gresk.modules.contract.application.command.CreateContractTemplateCommand;
import com.gresk.modules.contract.domain.model.ClauseTemplateId;
import com.gresk.modules.contract.domain.model.ContractTemplate;
import com.gresk.modules.contract.domain.port.out.ContractTemplateRepositoryPort;
import com.gresk.modules.promoter.domain.model.valueobject.PromoterId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class CreateContractTemplateUseCase {

    private final ContractTemplateRepositoryPort contractTemplateRepository;

    public ContractTemplate execute(CreateContractTemplateCommand cmd) {
        ContractTemplate template = ContractTemplate.create(
                PromoterId.of(cmd.promoterId()), cmd.type(), cmd.name(), cmd.bodyMarkdown(),
                cmd.variables(),
                cmd.defaultClauseTemplateIds().stream().map(ClauseTemplateId::of).toList());
        return contractTemplateRepository.save(template);
    }
}

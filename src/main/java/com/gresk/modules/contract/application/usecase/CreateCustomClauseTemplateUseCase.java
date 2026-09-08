package com.gresk.modules.contract.application.usecase;

import com.gresk.modules.contract.application.command.CreateClauseTemplateCommand;
import com.gresk.modules.contract.domain.model.ClauseTemplate;
import com.gresk.modules.contract.domain.port.out.ClauseTemplateRepositoryPort;
import com.gresk.modules.promoter.domain.model.valueobject.PromoterId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class CreateCustomClauseTemplateUseCase {

    private final ClauseTemplateRepositoryPort clauseTemplateRepository;

    public ClauseTemplate execute(CreateClauseTemplateCommand cmd) {
        ClauseTemplate template = ClauseTemplate.createCustom(
                PromoterId.of(cmd.promoterId()), cmd.code(), cmd.category(), cmd.title(),
                cmd.contentTemplate(), cmd.applicableTypes(), cmd.jurisdictionScope());
        return clauseTemplateRepository.save(template);
    }
}

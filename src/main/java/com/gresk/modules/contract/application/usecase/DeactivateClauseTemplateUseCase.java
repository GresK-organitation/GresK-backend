package com.gresk.modules.contract.application.usecase;

import com.gresk.modules.contract.domain.exception.ClauseTemplateNotFoundException;
import com.gresk.modules.contract.domain.exception.ClauseTemplateNotOwnedException;
import com.gresk.modules.contract.domain.model.ClauseTemplate;
import com.gresk.modules.contract.domain.model.ClauseTemplateId;
import com.gresk.modules.contract.domain.port.out.ClauseTemplateRepositoryPort;
import com.gresk.modules.promoter.domain.model.valueobject.PromoterId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class DeactivateClauseTemplateUseCase {

    private final ClauseTemplateRepositoryPort clauseTemplateRepository;

    public void execute(String templateId, String promoterId) {
        ClauseTemplate template = clauseTemplateRepository.findById(ClauseTemplateId.of(templateId))
                .orElseThrow(() -> new ClauseTemplateNotFoundException(templateId));
        if (!template.isOwnedBy(PromoterId.of(promoterId))) {
            throw new ClauseTemplateNotOwnedException();
        }
        template.deactivate();
        clauseTemplateRepository.save(template);
    }
}

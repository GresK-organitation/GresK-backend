package com.gresk.modules.contract.application.usecase;

import com.gresk.modules.contract.domain.model.ClauseTemplate;
import com.gresk.modules.contract.domain.model.ContractType;
import com.gresk.modules.contract.domain.port.out.ClauseTemplateRepositoryPort;
import com.gresk.modules.promoter.domain.model.valueobject.PromoterId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ListClauseTemplatesUseCase {

    private final ClauseTemplateRepositoryPort clauseTemplateRepository;

    /** Catálogo de sistema aplicable al tipo (si se indica) + cláusulas propias de la promotora. */
    public List<ClauseTemplate> execute(String promoterId, ContractType type) {
        List<ClauseTemplate> result = new ArrayList<>();
        if (type != null) {
            result.addAll(clauseTemplateRepository.findSystemDefaultsByType(type));
        }
        result.addAll(clauseTemplateRepository.findByPromoterId(PromoterId.of(promoterId)));
        return result;
    }
}

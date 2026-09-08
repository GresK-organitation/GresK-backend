package com.gresk.modules.contract.infrastructure.web;

import com.gresk.modules.contract.domain.model.ContractTemplate;
import com.gresk.modules.contract.infrastructure.web.dto.ContractTemplateResponse;
import org.springframework.stereotype.Component;

@Component
public class ContractTemplateResponseMapper {

    public ContractTemplateResponse toResponse(ContractTemplate t) {
        return new ContractTemplateResponse(
                t.getId().toString(), t.getType(), t.getName(), t.getBodyMarkdown(), t.getVariables(),
                t.getDefaultClauseTemplateIds().stream().map(Object::toString).toList(),
                t.getVersion(), t.isActive(), t.getCreatedAt());
    }
}

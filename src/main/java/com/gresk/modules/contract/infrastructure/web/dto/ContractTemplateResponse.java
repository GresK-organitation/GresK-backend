package com.gresk.modules.contract.infrastructure.web.dto;

import com.gresk.modules.contract.domain.model.ContractType;
import com.gresk.modules.contract.domain.model.valueobject.TemplateVariable;

import java.time.Instant;
import java.util.List;

public record ContractTemplateResponse(
        String                 id,
        ContractType           type,
        String                 name,
        String                 bodyMarkdown,
        List<TemplateVariable> variables,
        List<String>           defaultClauseTemplateIds,
        int                    version,
        boolean                active,
        Instant                createdAt
) {}

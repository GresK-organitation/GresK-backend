package com.gresk.modules.contract.infrastructure.web.dto;

import com.gresk.modules.contract.domain.model.ContractType;
import com.gresk.modules.contract.domain.model.valueobject.ClauseCategory;

import java.util.List;

public record ClauseTemplateResponse(
        String         id,
        String         code,
        ClauseCategory category,
        String         title,
        String         contentTemplate,
        List<ContractType> applicableTypes,
        String         jurisdictionScope,
        boolean        systemDefault,
        int            version,
        boolean        active
) {}

package com.gresk.modules.contract.application.command;

import com.gresk.modules.contract.domain.model.ContractType;
import com.gresk.modules.contract.domain.model.valueobject.ClauseCategory;

import java.util.List;

public record CreateClauseTemplateCommand(
        String            promoterId,
        String            code,
        ClauseCategory    category,
        String            title,
        String            contentTemplate,
        List<ContractType> applicableTypes,
        String            jurisdictionScope
) {}

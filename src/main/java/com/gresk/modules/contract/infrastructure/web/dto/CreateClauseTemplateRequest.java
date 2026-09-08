package com.gresk.modules.contract.infrastructure.web.dto;

import com.gresk.modules.contract.domain.model.ContractType;
import com.gresk.modules.contract.domain.model.valueobject.ClauseCategory;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record CreateClauseTemplateRequest(
        @NotBlank String     code,
        @NotNull ClauseCategory category,
        @NotBlank String     title,
        @NotBlank String     contentTemplate,
        List<ContractType>   applicableTypes,
        String                jurisdictionScope
) {}

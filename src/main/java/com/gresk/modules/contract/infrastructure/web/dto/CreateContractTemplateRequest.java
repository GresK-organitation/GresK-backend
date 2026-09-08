package com.gresk.modules.contract.infrastructure.web.dto;

import com.gresk.modules.contract.domain.model.ContractType;
import com.gresk.modules.contract.domain.model.valueobject.TemplateVariable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record CreateContractTemplateRequest(
        @NotNull ContractType   type,
        @NotBlank String        name,
        @NotBlank String        bodyMarkdown,
        List<TemplateVariable>  variables,
        List<String>            defaultClauseTemplateIds
) {}

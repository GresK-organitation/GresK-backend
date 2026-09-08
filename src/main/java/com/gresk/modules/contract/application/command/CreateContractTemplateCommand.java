package com.gresk.modules.contract.application.command;

import com.gresk.modules.contract.domain.model.ContractType;
import com.gresk.modules.contract.domain.model.valueobject.TemplateVariable;

import java.util.List;

public record CreateContractTemplateCommand(
        String                 promoterId,
        ContractType           type,
        String                 name,
        String                 bodyMarkdown,
        List<TemplateVariable> variables,
        List<String>           defaultClauseTemplateIds
) {}

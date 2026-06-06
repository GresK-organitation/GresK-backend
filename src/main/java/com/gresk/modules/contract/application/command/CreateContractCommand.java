package com.gresk.modules.contract.application.command;

import com.gresk.modules.contract.domain.model.ContractType;

public record CreateContractCommand(
        String       promoterId,
        ContractType type,
        String       partyAName,
        String       partyATaxId,
        String       partyAAddress,
        String       partyASignatoryName,
        String       partyASignatoryRole,
        String       partyAEmail
) {}

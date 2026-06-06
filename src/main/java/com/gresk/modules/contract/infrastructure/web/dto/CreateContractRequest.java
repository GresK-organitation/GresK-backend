package com.gresk.modules.contract.infrastructure.web.dto;

import com.gresk.modules.contract.domain.model.ContractType;
import jakarta.validation.constraints.NotNull;

public record CreateContractRequest(
        @NotNull ContractType type,
        String partyAName,
        String partyATaxId,
        String partyAAddress,
        String partyASignatoryName,
        String partyASignatoryRole,
        String partyAEmail
) {}

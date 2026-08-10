package com.gresk.modules.contract.domain.model.valueobject;

public record ContractParty(
        String name,
        String taxId,
        String address,
        String signatoryName,
        String signatoryRole,
        String email
) {}

package com.gresk.modules.contract.infrastructure.web.dto;

public record ContractPartyRequest(
        String name,
        String taxId,
        String address,
        String signatoryName,
        String signatoryRole,
        String email
) {}

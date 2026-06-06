package com.gresk.modules.contract.infrastructure.web.dto;

import com.gresk.modules.contract.domain.model.ContractStatus;
import com.gresk.modules.contract.domain.model.ContractType;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

public record ContractSummaryResponse(
        String         id,
        String         referenceNumber,
        ContractType   type,
        ContractStatus status,
        String         partyBName,
        BigDecimal     feeAmount,
        String         feeCurrency,
        LocalDate      contractDate,
        Instant        updatedAt
) {}

package com.gresk.modules.contract.infrastructure.web.dto;

import com.gresk.modules.contract.domain.model.ContractVersionStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public record ContractVersionResponse(
        String                 id,
        int                    versionNumber,
        ContractVersionStatus  status,
        ContractResponse.PartyResponse partyA,
        ContractResponse.PartyResponse partyB,
        ContractResponse.PerformanceResponse performanceDetails,
        BigDecimal             feeAmount,
        List<ContractResponse.ClauseResponse> clauses,
        String                 changeSummary,
        String                 createdBy,
        Instant                createdAt
) {}

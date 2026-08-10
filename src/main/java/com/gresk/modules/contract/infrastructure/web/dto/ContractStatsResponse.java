package com.gresk.modules.contract.infrastructure.web.dto;

import com.gresk.modules.contract.domain.model.ContractType;

import java.math.BigDecimal;
import java.util.Map;

public record ContractStatsResponse(
        long                    draftCount,
        long                    sentCount,
        long                    signedCount,
        long                    archivedCount,
        long                    cancelledCount,
        BigDecimal              totalSignedFeeValue,
        Map<ContractType, Long> countByType
) {}

package com.gresk.modules.contract.domain.port.out;

import com.gresk.modules.contract.domain.model.ContractType;

import java.math.BigDecimal;
import java.util.Map;

public record ContractStats(
        long                      draftCount,
        long                      sentCount,
        long                      deliveredCount,
        long                      signedCount,
        long                      archivedCount,
        long                      cancelledCount,
        long                      voidedCount,
        BigDecimal                totalSignedFeeValue,
        Map<ContractType, Long>   countByType
) {}

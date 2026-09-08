package com.gresk.modules.finance.infrastructure.web.dto;

import java.math.BigDecimal;
import java.util.List;

public record EventFinancialPlanResponse(
        String id,
        String linkedEventId,
        BigDecimal deviationThresholdPercentage,
        List<CostLineResponse> costLines
) {
}

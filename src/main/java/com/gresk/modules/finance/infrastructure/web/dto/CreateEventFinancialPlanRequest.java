package com.gresk.modules.finance.infrastructure.web.dto;

import java.math.BigDecimal;

public record CreateEventFinancialPlanRequest(
        BigDecimal deviationThresholdPercentage
) {
}

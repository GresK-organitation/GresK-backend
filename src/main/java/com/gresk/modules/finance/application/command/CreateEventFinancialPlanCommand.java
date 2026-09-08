package com.gresk.modules.finance.application.command;

import java.math.BigDecimal;

public record CreateEventFinancialPlanCommand(
        String promoterId,
        String linkedEventId,
        BigDecimal deviationThresholdPercentage
) {
}

package com.gresk.modules.show.infrastructure.web.dto;

import java.math.BigDecimal;

public record FinancialViabilityReportResponse(
        BigDecimal totalFixedCosts,
        BigDecimal variableCostPerAttendee,
        BigDecimal contributionMarginPerTicket,
        Integer    breakEvenAttendees,
        BigDecimal breakEvenPercentOfCapacity,
        int        projectedAttendance,
        BigDecimal projectedRevenue,
        BigDecimal projectedProfit,
        BigDecimal marginOfSafetyPercent,
        boolean    viable
) {}

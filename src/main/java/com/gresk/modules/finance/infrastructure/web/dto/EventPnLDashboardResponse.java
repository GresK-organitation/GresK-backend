package com.gresk.modules.finance.infrastructure.web.dto;

import com.gresk.modules.finance.domain.model.valueobject.CostSubcategory;

import java.math.BigDecimal;
import java.util.Map;

public record EventPnLDashboardResponse(
        BigDecimal grossRevenue,
        BigDecimal totalFixedCosts,
        BigDecimal totalVariableCosts,
        BigDecimal netProfit,
        String currency,
        Map<CostSubcategory, BigDecimal> costBreakdown,
        int ticketsSold
) {
}

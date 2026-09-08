package com.gresk.modules.finance.infrastructure.web.dto;

import com.gresk.modules.finance.domain.model.valueobject.CostCategory;
import com.gresk.modules.finance.domain.model.valueobject.CostSubcategory;

import java.math.BigDecimal;

public record CostLineRequest(
        CostCategory category,
        CostSubcategory subcategory,
        String description,
        BigDecimal budgetedAmount,
        BigDecimal variablePercentage,
        String currency
) {
}

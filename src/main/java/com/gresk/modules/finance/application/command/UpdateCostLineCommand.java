package com.gresk.modules.finance.application.command;

import com.gresk.modules.finance.domain.model.valueobject.CostCategory;
import com.gresk.modules.finance.domain.model.valueobject.CostSubcategory;

import java.math.BigDecimal;

public record UpdateCostLineCommand(
        String planId,
        String costLineId,
        String promoterId,
        CostCategory category,
        CostSubcategory subcategory,
        String description,
        BigDecimal budgetedAmount,
        BigDecimal variablePercentage,
        String currency
) {
}

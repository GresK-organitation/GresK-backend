package com.gresk.modules.show.application.command;

import com.gresk.modules.show.domain.model.valueobject.CostCategory;
import com.gresk.modules.show.domain.model.valueobject.CostNature;

import java.math.BigDecimal;

public record CostLineItemInput(CostCategory category, String label, BigDecimal amount, CostNature nature) {
}

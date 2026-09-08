package com.gresk.modules.show.domain.model.valueobject;

import java.math.BigDecimal;

public record CostLineItem(CostCategory category, String label, BigDecimal amount, CostNature nature) {

    public CostLineItem {
        if (category == null) {
            throw new IllegalArgumentException("CostLineItem category must not be null");
        }
        if (label == null || label.isBlank()) {
            throw new IllegalArgumentException("CostLineItem label must not be blank");
        }
        if (amount == null || amount.signum() < 0) {
            throw new IllegalArgumentException("CostLineItem amount must not be negative");
        }
        if (nature == null) {
            throw new IllegalArgumentException("CostLineItem nature must not be null");
        }
    }
}

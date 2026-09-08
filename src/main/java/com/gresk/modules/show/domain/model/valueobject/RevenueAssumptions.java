package com.gresk.modules.show.domain.model.valueobject;

import com.gresk.shared.domain.valueobject.Percentage;

import java.math.BigDecimal;

public record RevenueAssumptions(BigDecimal avgTicketPrice, Percentage expectedSelloutPercent, String currency) {

    public RevenueAssumptions {
        if (avgTicketPrice == null || avgTicketPrice.signum() <= 0) {
            throw new IllegalArgumentException("RevenueAssumptions avgTicketPrice must be positive");
        }
        if (expectedSelloutPercent == null) {
            throw new IllegalArgumentException("RevenueAssumptions expectedSelloutPercent must not be null");
        }
        if (currency == null || currency.isBlank()) {
            throw new IllegalArgumentException("RevenueAssumptions currency must not be blank");
        }
    }
}

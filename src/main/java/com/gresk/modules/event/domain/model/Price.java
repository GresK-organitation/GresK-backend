package com.gresk.modules.event.domain.model;

import java.math.BigDecimal;
import java.util.Objects;

public record Price(BigDecimal amount, String currency) {

    public Price {
        Objects.requireNonNull(amount, "Price amount must not be null");
        Objects.requireNonNull(currency, "Price currency must not be null");
        if (currency.isBlank()) {
            throw new IllegalArgumentException("Price currency must not be blank");
        }
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Price amount must be greater than 0");
        }
    }
}

package com.gresk.modules.finance.domain.model.valueobject;

import com.gresk.shared.domain.valueobject.Money;

import java.math.BigDecimal;

public record InvoiceLine(
        String description,
        int quantity,
        Money unitPrice,
        BigDecimal taxRatePercentage
) {
    public InvoiceLine {
        if (description == null || description.isBlank()) throw new IllegalArgumentException("description is required");
        if (quantity <= 0) throw new IllegalArgumentException("quantity must be positive");
        if (unitPrice == null) throw new IllegalArgumentException("unitPrice is required");
        if (taxRatePercentage == null || taxRatePercentage.signum() < 0
                || taxRatePercentage.compareTo(BigDecimal.valueOf(100)) > 0) {
            throw new IllegalArgumentException("taxRatePercentage must be between 0 and 100");
        }
    }

    public Money lineSubtotal() {
        return unitPrice.multiply(quantity);
    }

    public Money lineTax() {
        return lineSubtotal().multiply(taxRatePercentage.movePointLeft(2));
    }

    public Money lineTotal() {
        return lineSubtotal().add(lineTax());
    }
}

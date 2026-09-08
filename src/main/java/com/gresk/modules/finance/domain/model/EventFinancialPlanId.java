package com.gresk.modules.finance.domain.model;

import java.util.UUID;

public record EventFinancialPlanId(UUID value) {

    public EventFinancialPlanId {
        if (value == null) throw new IllegalArgumentException("EventFinancialPlanId cannot be null");
    }

    public static EventFinancialPlanId generate() {
        return new EventFinancialPlanId(UUID.randomUUID());
    }

    public static EventFinancialPlanId of(String value) {
        try {
            return new EventFinancialPlanId(UUID.fromString(value));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid EventFinancialPlanId format: " + value, e);
        }
    }

    public static EventFinancialPlanId of(UUID value) {
        return new EventFinancialPlanId(value);
    }

    @Override
    public String toString() {
        return value.toString();
    }
}

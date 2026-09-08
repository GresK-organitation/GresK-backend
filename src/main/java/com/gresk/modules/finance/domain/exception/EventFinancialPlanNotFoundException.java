package com.gresk.modules.finance.domain.exception;

public class EventFinancialPlanNotFoundException extends RuntimeException {
    public EventFinancialPlanNotFoundException(String id) {
        super("Event financial plan not found: " + id);
    }
}

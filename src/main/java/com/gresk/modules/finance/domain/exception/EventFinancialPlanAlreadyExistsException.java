package com.gresk.modules.finance.domain.exception;

public class EventFinancialPlanAlreadyExistsException extends RuntimeException {
    public EventFinancialPlanAlreadyExistsException(String linkedEventId) {
        super("A financial plan already exists for event: " + linkedEventId);
    }
}

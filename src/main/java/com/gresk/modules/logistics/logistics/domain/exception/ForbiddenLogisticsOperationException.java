package com.gresk.modules.logistics.domain.exception;

public class ForbiddenLogisticsOperationException extends RuntimeException {
    public ForbiddenLogisticsOperationException(String message) {
        super(message);
    }
}

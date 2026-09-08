package com.gresk.modules.booking.domain.exception;

public class ForbiddenBookingOperationException extends RuntimeException {
    public ForbiddenBookingOperationException(String message) {
        super(message);
    }
}

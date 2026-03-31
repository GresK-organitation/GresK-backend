package com.gresk.modules.event.domain.exception;

public class InvalidEventTransitionException extends RuntimeException {
    public InvalidEventTransitionException(String message) {
        super(message);
    }
}

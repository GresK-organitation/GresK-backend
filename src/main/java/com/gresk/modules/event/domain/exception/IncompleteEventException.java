package com.gresk.modules.event.domain.exception;

public class IncompleteEventException extends RuntimeException {
    public IncompleteEventException(String message) {
        super(message);
    }
}

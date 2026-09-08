package com.gresk.modules.finance.domain.exception;

public class EventNotFoundException extends RuntimeException {
    public EventNotFoundException(String id) {
        super("Event not found: " + id);
    }
}

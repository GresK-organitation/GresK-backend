package com.gresk.modules.quotation.domain.exception;

public class EventQuoteAlreadyExistsException extends RuntimeException {
    public EventQuoteAlreadyExistsException(String eventId) {
        super("A quote already exists for event: " + eventId);
    }
}

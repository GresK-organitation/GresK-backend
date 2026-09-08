package com.gresk.modules.quotation.domain.exception;

public class EventQuoteNotFoundException extends RuntimeException {
    public EventQuoteNotFoundException(String eventId) {
        super("No quote found for event: " + eventId);
    }
}

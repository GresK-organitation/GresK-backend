package com.gresk.modules.quotation.domain.exception;

public class EventHasNoArtistException extends RuntimeException {
    public EventHasNoArtistException(String eventId) {
        super("Event has no artist assigned, cannot generate a quote: " + eventId);
    }
}

package com.gresk.modules.venue.domain.exception;

public class VenueNotFoundException extends RuntimeException {
    public VenueNotFoundException(String id) {
        super("Venue not found: " + id);
    }
}

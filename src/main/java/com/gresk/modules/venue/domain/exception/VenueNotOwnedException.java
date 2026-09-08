package com.gresk.modules.venue.domain.exception;

public class VenueNotOwnedException extends RuntimeException {
    public VenueNotOwnedException() {
        super("You do not have permission to access this venue");
    }
}

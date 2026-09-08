package com.gresk.modules.show.domain.exception;

public class VenueHoldExpiredException extends RuntimeException {
    public VenueHoldExpiredException(String showId) {
        super("Venue hold has expired for show: " + showId);
    }
}

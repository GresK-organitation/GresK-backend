package com.gresk.modules.logistics.domain.exception;

public class ItineraryNotFoundException extends RuntimeException {
    public ItineraryNotFoundException(String itineraryId) {
        super("Itinerary not found: " + itineraryId);
    }
}

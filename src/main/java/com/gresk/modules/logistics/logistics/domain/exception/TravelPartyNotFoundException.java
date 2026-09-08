package com.gresk.modules.logistics.domain.exception;

public class TravelPartyNotFoundException extends RuntimeException {
    public TravelPartyNotFoundException(String travelPartyId) {
        super("Travel party not found: " + travelPartyId);
    }
}

package com.gresk.modules.rider.domain.exception;

public class RiderNotFoundException extends RuntimeException {
    public RiderNotFoundException(String riderId) {
        super("Rider not found: " + riderId);
    }
}

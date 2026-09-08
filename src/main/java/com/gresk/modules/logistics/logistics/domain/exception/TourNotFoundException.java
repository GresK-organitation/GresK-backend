package com.gresk.modules.logistics.domain.exception;

public class TourNotFoundException extends RuntimeException {
    public TourNotFoundException(String tourId) {
        super("Tour not found: " + tourId);
    }
}

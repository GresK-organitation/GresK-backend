package com.gresk.modules.logistics.domain.exception;

public class InvalidItineraryException extends RuntimeException {
    public InvalidItineraryException(String message) {
        super(message);
    }
}

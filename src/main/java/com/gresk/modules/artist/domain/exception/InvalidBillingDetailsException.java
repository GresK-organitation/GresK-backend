package com.gresk.modules.artist.domain.exception;

public class InvalidBillingDetailsException extends RuntimeException {
    public InvalidBillingDetailsException(String message) {
        super(message);
    }
}

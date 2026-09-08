package com.gresk.modules.logistics.domain.exception;

public class InvalidTourException extends RuntimeException {
    public InvalidTourException(String message) {
        super(message);
    }
}

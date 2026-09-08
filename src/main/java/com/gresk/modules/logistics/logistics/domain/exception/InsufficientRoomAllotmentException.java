package com.gresk.modules.logistics.domain.exception;

public class InsufficientRoomAllotmentException extends RuntimeException {
    public InsufficientRoomAllotmentException(String message) {
        super(message);
    }
}

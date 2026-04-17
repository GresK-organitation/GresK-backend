package com.gresk.modules.artist.domain.exception;

public class InvalidFollowerCountException extends RuntimeException {
    public InvalidFollowerCountException(String message) {
        super(message);
    }
}

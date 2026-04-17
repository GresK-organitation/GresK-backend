package com.gresk.modules.artist.domain.exception;

public class InvalidSocialUrlException extends RuntimeException {
    public InvalidSocialUrlException(String message) {
        super(message);
    }
}

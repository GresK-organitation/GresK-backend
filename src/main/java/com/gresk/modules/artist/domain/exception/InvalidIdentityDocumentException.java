package com.gresk.modules.artist.domain.exception;

public class InvalidIdentityDocumentException extends RuntimeException {
    public InvalidIdentityDocumentException(String message) {
        super(message);
    }
}

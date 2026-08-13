package com.gresk.modules.curation.domain.exception;

public class InvalidCuratedListException extends RuntimeException {
    public InvalidCuratedListException(String message) {
        super(message);
    }
}

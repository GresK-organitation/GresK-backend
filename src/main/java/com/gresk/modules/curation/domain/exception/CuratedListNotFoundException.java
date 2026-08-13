package com.gresk.modules.curation.domain.exception;

public class CuratedListNotFoundException extends RuntimeException {
    public CuratedListNotFoundException(String message) {
        super(message);
    }
}

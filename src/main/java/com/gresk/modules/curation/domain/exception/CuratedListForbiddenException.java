package com.gresk.modules.curation.domain.exception;

public class CuratedListForbiddenException extends RuntimeException {
    public CuratedListForbiddenException(String message) {
        super(message);
    }
}

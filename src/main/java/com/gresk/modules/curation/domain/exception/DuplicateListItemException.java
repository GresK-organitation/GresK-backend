package com.gresk.modules.curation.domain.exception;

public class DuplicateListItemException extends RuntimeException {
    public DuplicateListItemException(String message) {
        super(message);
    }
}

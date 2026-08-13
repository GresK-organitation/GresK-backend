package com.gresk.modules.curation.domain.exception;

public class ReferencedEntryNotFoundException extends RuntimeException {
    public ReferencedEntryNotFoundException(String message) {
        super(message);
    }
}

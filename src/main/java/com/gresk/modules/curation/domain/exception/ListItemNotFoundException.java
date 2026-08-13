package com.gresk.modules.curation.domain.exception;

public class ListItemNotFoundException extends RuntimeException {
    public ListItemNotFoundException(String message) {
        super(message);
    }
}

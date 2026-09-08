package com.gresk.modules.show.domain.exception;

public class ShowNotFoundException extends RuntimeException {
    public ShowNotFoundException(String id) {
        super("Show not found: " + id);
    }
}

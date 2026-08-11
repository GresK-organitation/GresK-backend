package com.gresk.modules.journal.domain.exception;

public class TooManyMediaItemsException extends RuntimeException {
    public TooManyMediaItemsException(String message) {
        super(message);
    }
}

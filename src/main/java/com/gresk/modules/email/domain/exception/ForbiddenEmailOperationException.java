package com.gresk.modules.email.domain.exception;

public class ForbiddenEmailOperationException extends RuntimeException {
    public ForbiddenEmailOperationException(String message) {
        super(message);
    }
}

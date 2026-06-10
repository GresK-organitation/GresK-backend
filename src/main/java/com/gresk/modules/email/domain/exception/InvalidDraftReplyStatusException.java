package com.gresk.modules.email.domain.exception;

public class InvalidDraftReplyStatusException extends RuntimeException {
    public InvalidDraftReplyStatusException(String message) {
        super(message);
    }
}

package com.gresk.shared.domain.exception;

public class InvalidPasswordException extends DomainException {
    public InvalidPasswordException(String message) {
        super(message);
    }
}

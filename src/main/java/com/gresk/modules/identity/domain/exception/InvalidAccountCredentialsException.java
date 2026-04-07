package com.gresk.modules.identity.domain.exception;

public class InvalidAccountCredentialsException extends RuntimeException {

    public InvalidAccountCredentialsException() {
        super("Invalid credentials");
    }
}

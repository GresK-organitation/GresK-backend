package com.gresk.modules.account.domain.exception;

public class InvalidAccountCredentialsException extends RuntimeException {

    public InvalidAccountCredentialsException() {
        super("Invalid credentials");
    }
}

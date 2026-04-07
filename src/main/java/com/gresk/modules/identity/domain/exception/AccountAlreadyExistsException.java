package com.gresk.modules.identity.domain.exception;

public class AccountAlreadyExistsException extends RuntimeException {

    public AccountAlreadyExistsException(String email) {
        super("Account already exists for email: " + email);
    }
}

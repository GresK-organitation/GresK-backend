package com.gresk.modules.account.domain.exception;

public class AccountAlreadyExistsException extends RuntimeException {

    public AccountAlreadyExistsException(String email) {
        super("Account already exists for email: " + email);
    }
}

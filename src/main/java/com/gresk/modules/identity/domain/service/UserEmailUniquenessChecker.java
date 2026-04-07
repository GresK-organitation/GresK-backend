package com.gresk.modules.identity.domain.service;

import com.gresk.modules.identity.domain.exception.AccountAlreadyExistsException;
import com.gresk.modules.identity.domain.port.out.AccountRepositoryPort;
import com.gresk.shared.domain.valueobject.Email;


public class UserEmailUniquenessChecker {

    private final AccountRepositoryPort accountRepositoryPort;

    public UserEmailUniquenessChecker(AccountRepositoryPort accountRepositoryPort) {
        this.accountRepositoryPort = accountRepositoryPort;
    }

    public void check(Email email) {
        if (accountRepositoryPort.existsByEmail(email)) {
            throw new AccountAlreadyExistsException(email.value());
        }
    }
}
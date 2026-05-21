package com.gresk.modules.account.application.handler;

import com.gresk.modules.account.domain.port.out.AccountRepositoryPort;
import com.gresk.shared.domain.valueobject.Email;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CheckEmailHandler {
    private final AccountRepositoryPort repository;

    public boolean execute(String email) {
        return repository.existsByEmail(Email.of(email));
    }
}

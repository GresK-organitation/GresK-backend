package com.gresk.modules.identity.application.usecase;

import com.gresk.modules.identity.domain.port.out.AccountRepositoryPort;
import com.gresk.shared.domain.valueobject.Email;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GetEmailUseCase {
    private final AccountRepositoryPort accountRepository;

    public boolean existsByEmail(String email) {
        return accountRepository.existsByEmail(Email.of(email));
    }
}

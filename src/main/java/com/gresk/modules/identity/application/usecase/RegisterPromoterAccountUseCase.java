package com.gresk.modules.identity.application.usecase;

import com.gresk.modules.identity.application.command.RegisterPromoterAccountCommand;
import com.gresk.modules.identity.application.port.in.RegisterUserAccountUseCase;
import com.gresk.modules.identity.domain.model.Account;
import com.gresk.modules.identity.domain.model.AccountId;
import com.gresk.modules.identity.domain.port.out.AccountRepositoryPort;
import com.gresk.shared.domain.AccountStatus;
import com.gresk.shared.domain.Role;
import com.gresk.shared.domain.valueobject.Email;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RegisterPromoterAccountUseCase {
    private final RegisterUserAccountUseCase registerUserAccountUseCase;
    private final AccountRepositoryPort accountRepository;
    private final ApplicationEventPublisher eventPublisher;

    public AccountId execute(RegisterPromoterAccountCommand command) {
        Email email = Email.of(command.email());
        if (accountRepository.existsByEmail(email))
            throw new RuntimeException("Account with email already exists");

        AccountStatus status = command.roles().contains(Role.PROMOTER)
                ? AccountStatus.PENDING
                : AccountStatus.ACTIVE;
        return accountRepository.save(Account.create(email, command.rawPassword(), command.roles(), status));
    }
}

package com.gresk.modules.identity.application.usecase;

import com.gresk.modules.identity.application.command.RegisterAccountCommand;
import com.gresk.modules.identity.application.port.in.RegisterAccountUseCase;
import com.gresk.modules.identity.application.port.out.PasswordHasherPort;
import com.gresk.modules.identity.domain.exception.AccountAlreadyExistsException;
import com.gresk.modules.identity.domain.model.Account;
import com.gresk.modules.identity.domain.model.AccountId;
import com.gresk.modules.identity.domain.port.out.AccountRepositoryPort;
import com.gresk.shared.domain.AccountStatus;
import com.gresk.shared.domain.Role;
import com.gresk.shared.domain.valueobject.Email;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RegisterAccountUseCaseImpl implements RegisterAccountUseCase {

    private final AccountRepositoryPort accountRepositoryPort;
    private final PasswordHasherPort passwordHasherPort;

    @Transactional
    @Override
    public AccountId execute(RegisterAccountCommand command) {
        Email email = new Email(command.email());

        if (accountRepositoryPort.existsByEmail(email)) {
            throw new AccountAlreadyExistsException(command.email());
        }

        String passwordHash = passwordHasherPort.hash(command.rawPassword());

        AccountStatus status = command.roles().contains(Role.PROMOTER)
                ? AccountStatus.PENDING
                : AccountStatus.ACTIVE;

        Account account = Account.create(email, passwordHash, command.roles(), status);

        return accountRepositoryPort.save(account);
    }
}

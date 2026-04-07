package com.gresk.modules.identity.application.usecase;

import com.gresk.infrastructure.port.AuthToken;
import com.gresk.modules.identity.application.command.LoginCommand;
import com.gresk.modules.identity.application.port.in.LoginUseCase;
import com.gresk.modules.identity.application.port.out.JwtTokenGeneratorPort;
import com.gresk.modules.identity.application.port.out.PasswordHasherPort;
import com.gresk.modules.identity.domain.exception.InvalidAccountCredentialsException;
import com.gresk.modules.identity.domain.port.out.AccountRepositoryPort;
import com.gresk.shared.domain.AccountStatus;
import com.gresk.shared.domain.valueobject.Email;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class LoginUseCaseImpl implements LoginUseCase {

    private final AccountRepositoryPort accountRepositoryPort;
    private final PasswordHasherPort passwordHasherPort;
    private final JwtTokenGeneratorPort jwtTokenGeneratorPort;

    @Transactional(readOnly = true)
    @Override
    public AuthToken execute(LoginCommand command) {
        Email email = new Email(command.email());

        var account = accountRepositoryPort.findByEmail(email)
                .orElseThrow(InvalidAccountCredentialsException::new);

        if (account.getStatus() != AccountStatus.ACTIVE) {
            throw new InvalidAccountCredentialsException();
        }

        if (!passwordHasherPort.matches(command.rawPassword(), account.getPasswordHash())) {
            throw new InvalidAccountCredentialsException();
        }

        return jwtTokenGeneratorPort.generate(account.getId(), account.getEmail(), account.getRoles());
    }
}

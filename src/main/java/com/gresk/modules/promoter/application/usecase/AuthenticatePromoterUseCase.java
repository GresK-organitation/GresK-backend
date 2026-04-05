package com.gresk.modules.promoter.application.usecase;

import com.gresk.infrastructure.port.AuthToken;
import com.gresk.infrastructure.port.JwtTokenGenerator;
import com.gresk.modules.promoter.application.command.AuthenticatePromoterCommand;
import com.gresk.modules.promoter.application.port.in.AuthenticatePromoterPort;
import com.gresk.modules.promoter.application.port.out.PasswordHasher;
import com.gresk.modules.promoter.domain.PromoterStatus;
import com.gresk.modules.promoter.domain.exception.InvalidCredentialsException;
import com.gresk.modules.promoter.domain.port.out.PromoterRepository;
import com.gresk.modules.promoter.domain.valueobject.Email;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthenticatePromoterUseCase implements AuthenticatePromoterPort {

    private final PromoterRepository promoterRepository;
    private final PasswordHasher passwordHasher;
    private final JwtTokenGenerator jwtTokenGenerator;

    @Transactional(readOnly = true)
    @Override
    public AuthToken execute(AuthenticatePromoterCommand command) {
        Email email = new Email(command.email());

        var promoter = promoterRepository.findByEmail(email)
                .orElseThrow(InvalidCredentialsException::new);

        if (promoter.getStatus() != PromoterStatus.ACTIVE) {
            throw new InvalidCredentialsException();
        }

        if (!passwordHasher.matches(command.rawPassword(), promoter.getPassword().hashedValue())) {
            throw new InvalidCredentialsException();
        }

        return jwtTokenGenerator.generate(promoter.getId(), promoter.getEmail());
    }
}

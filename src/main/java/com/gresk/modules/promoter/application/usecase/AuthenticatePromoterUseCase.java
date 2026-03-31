package com.gresk.modules.promoter.application.usecase;

import com.gresk.modules.promoter.domain.PromoterStatus;
import com.gresk.modules.promoter.application.command.AuthenticatePromoterCommand;
import com.gresk.modules.promoter.domain.exception.InvalidCredentialsException;
import com.gresk.modules.promoter.port.PromoterRepository;
import com.gresk.modules.promoter.domain.valueobject.Email;
import com.gresk.shared.port.AuthToken;
import com.gresk.shared.port.JwtTokenGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Service
@RequiredArgsConstructor
public class AuthenticatePromoterUseCase {

    private final PromoterRepository promoterRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenGenerator jwtTokenGenerator;

    public Mono<AuthToken> execute(AuthenticatePromoterCommand command) {
        return Mono.defer(() -> {
            Email email = new Email(command.email());
            return promoterRepository.findByEmail(email)
                    .switchIfEmpty(Mono.error(new InvalidCredentialsException()))
                    .flatMap(promoter ->
                            Mono.fromCallable(() -> {
                                        if (promoter.getStatus() != PromoterStatus.ACTIVE) {
                                            throw new InvalidCredentialsException();
                                        }
                                        boolean matches = passwordEncoder.matches(
                                                command.rawPassword(),
                                                promoter.getPassword().hashedValue()
                                        );
                                        if (!matches) {
                                            throw new InvalidCredentialsException();
                                        }
                                        return jwtTokenGenerator.generate(
                                                promoter.getId(),
                                                promoter.getEmail()
                                        );
                                    })
                                    .subscribeOn(Schedulers.boundedElastic())
                    );
        });
    }
}

package com.gresk.modules.promoter.application.usecase;

import com.gresk.modules.promoter.domain.PromoterStatus;
import com.gresk.modules.promoter.application.command.AuthenticatePromoterCommand;
import com.gresk.modules.promoter.domain.exception.InvalidCredentialsException;
import com.gresk.modules.promoter.domain.model.Promoter;
import com.gresk.modules.promoter.port.PromoterRepository;
import com.gresk.modules.promoter.domain.valueobject.*;
import com.gresk.shared.port.AuthToken;
import com.gresk.shared.port.JwtTokenGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthenticatePromoterUseCaseTest {

    @Mock private PromoterRepository promoterRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private JwtTokenGenerator jwtTokenGenerator;
    @InjectMocks private AuthenticatePromoterUseCase useCase;

    private static final String RAW_EMAIL    = "promoter@gresk.com";
    private static final String RAW_PASSWORD = "securePassword123";
    private static final String HASHED_PW   = "$2a$10$hashedPassword";

    private AuthenticatePromoterCommand command;

    @BeforeEach
    void setUp() {
        command = new AuthenticatePromoterCommand(RAW_EMAIL, RAW_PASSWORD);
    }

    private Promoter buildPromoter(PromoterStatus status) {
        return Promoter.reconstitute(
                PromoterId.generate(),
                new Email(RAW_EMAIL),
                new Password(HASHED_PW),
                new PromoterName("Club Nocturno"),
                new Description("A club"),
                new Location("Madrid", "España", null),
                Set.of(),
                status,
                LocalDateTime.now(),
                status == PromoterStatus.ACTIVE
        );
    }

    @Test
    void execute_shouldReturnAuthTokenOnSuccessfulLogin() {
        Promoter activePromoter = buildPromoter(PromoterStatus.ACTIVE);
        when(promoterRepository.findByEmail(any(Email.class))).thenReturn(Mono.just(activePromoter));
        when(passwordEncoder.matches(eq(RAW_PASSWORD), eq(HASHED_PW))).thenReturn(true);
        when(jwtTokenGenerator.generate(any(PromoterId.class), any(Email.class)))
                .thenReturn(new AuthToken("eyJhbGciOiJIUzI1NiJ9.payload.sig", 3600L));

        StepVerifier.create(useCase.execute(command))
                .assertNext(token -> assertThat(token.token()).isNotBlank())
                .verifyComplete();
    }

    @Test
    void execute_shouldThrowInvalidCredentialsExceptionWhenEmailNotFound() {
        when(promoterRepository.findByEmail(any(Email.class))).thenReturn(Mono.empty());

        StepVerifier.create(useCase.execute(command))
                .expectError(InvalidCredentialsException.class)
                .verify();

        verify(passwordEncoder, never()).matches(any(), any());
        verify(jwtTokenGenerator, never()).generate(any(), any());
    }

    @Test
    void execute_shouldThrowInvalidCredentialsExceptionWhenPasswordIsWrong() {
        Promoter activePromoter = buildPromoter(PromoterStatus.ACTIVE);
        when(promoterRepository.findByEmail(any(Email.class))).thenReturn(Mono.just(activePromoter));
        when(passwordEncoder.matches(eq(RAW_PASSWORD), eq(HASHED_PW))).thenReturn(false);

        StepVerifier.create(useCase.execute(command))
                .expectError(InvalidCredentialsException.class)
                .verify();

        verify(jwtTokenGenerator, never()).generate(any(), any());
    }

    @Test
    void execute_shouldThrowInvalidCredentialsExceptionWhenPromoterIsPending() {
        when(promoterRepository.findByEmail(any(Email.class)))
                .thenReturn(Mono.just(buildPromoter(PromoterStatus.PENDING)));

        StepVerifier.create(useCase.execute(command))
                .expectError(InvalidCredentialsException.class)
                .verify();

        verify(passwordEncoder, never()).matches(any(), any());
        verify(jwtTokenGenerator, never()).generate(any(), any());
    }

    @Test
    void execute_shouldThrowInvalidCredentialsExceptionWhenPromoterIsSuspended() {
        when(promoterRepository.findByEmail(any(Email.class)))
                .thenReturn(Mono.just(buildPromoter(PromoterStatus.SUSPENDED)));

        StepVerifier.create(useCase.execute(command))
                .expectError(InvalidCredentialsException.class)
                .verify();

        verify(passwordEncoder, never()).matches(any(), any());
        verify(jwtTokenGenerator, never()).generate(any(), any());
    }
}

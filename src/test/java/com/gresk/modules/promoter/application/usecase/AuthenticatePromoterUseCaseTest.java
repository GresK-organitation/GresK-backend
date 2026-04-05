package com.gresk.modules.promoter.application.usecase;

import com.gresk.infrastructure.port.AuthToken;
import com.gresk.infrastructure.port.JwtTokenGenerator;
import com.gresk.modules.promoter.application.command.AuthenticatePromoterCommand;
import com.gresk.modules.promoter.domain.PromoterStatus;
import com.gresk.modules.promoter.domain.exception.InvalidCredentialsException;
import com.gresk.modules.promoter.domain.model.Promoter;
import com.gresk.modules.promoter.domain.valueobject.*;
import com.gresk.modules.promoter.application.port.out.PasswordHasher;
import com.gresk.modules.promoter.domain.port.out.PromoterRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthenticatePromoterUseCaseTest {

    @Mock private PromoterRepository promoterRepository;
    @Mock private PasswordHasher passwordHasher;
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
        when(promoterRepository.findByEmail(any(Email.class))).thenReturn(Optional.of(activePromoter));
        when(passwordHasher.matches(eq(RAW_PASSWORD), eq(HASHED_PW))).thenReturn(true);
        when(jwtTokenGenerator.generate(any(PromoterId.class), any(Email.class)))
                .thenReturn(new AuthToken("eyJhbGciOiJIUzI1NiJ9.payload.sig", 3600L));

        AuthToken token = useCase.execute(command);

        assertThat(token.token()).isNotBlank();
    }

    @Test
    void execute_shouldThrowInvalidCredentialsExceptionWhenEmailNotFound() {
        when(promoterRepository.findByEmail(any(Email.class))).thenReturn(Optional.empty());

        assertThrows(InvalidCredentialsException.class, () -> useCase.execute(command));

        verify(passwordHasher, never()).matches(any(), any());
        verify(jwtTokenGenerator, never()).generate(any(), any());
    }

    @Test
    void execute_shouldThrowInvalidCredentialsExceptionWhenPasswordIsWrong() {
        Promoter activePromoter = buildPromoter(PromoterStatus.ACTIVE);
        when(promoterRepository.findByEmail(any(Email.class))).thenReturn(Optional.of(activePromoter));
        when(passwordHasher.matches(eq(RAW_PASSWORD), eq(HASHED_PW))).thenReturn(false);

        assertThrows(InvalidCredentialsException.class, () -> useCase.execute(command));

        verify(jwtTokenGenerator, never()).generate(any(), any());
    }

    @Test
    void execute_shouldThrowInvalidCredentialsExceptionWhenPromoterIsPending() {
        when(promoterRepository.findByEmail(any(Email.class)))
                .thenReturn(Optional.of(buildPromoter(PromoterStatus.PENDING)));

        assertThrows(InvalidCredentialsException.class, () -> useCase.execute(command));

        verify(passwordHasher, never()).matches(any(), any());
        verify(jwtTokenGenerator, never()).generate(any(), any());
    }

    @Test
    void execute_shouldThrowInvalidCredentialsExceptionWhenPromoterIsSuspended() {
        when(promoterRepository.findByEmail(any(Email.class)))
                .thenReturn(Optional.of(buildPromoter(PromoterStatus.SUSPENDED)));

        assertThrows(InvalidCredentialsException.class, () -> useCase.execute(command));

        verify(passwordHasher, never()).matches(any(), any());
        verify(jwtTokenGenerator, never()).generate(any(), any());
    }
}

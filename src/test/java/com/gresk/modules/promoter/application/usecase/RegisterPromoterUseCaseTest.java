package com.gresk.modules.promoter.application.usecase;

import com.gresk.modules.promoter.MusicGenre;
import com.gresk.modules.promoter.application.command.RegisterPromoterCommand;
import com.gresk.modules.promoter.exception.EmailAlreadyExistsException;
import com.gresk.modules.promoter.model.Promoter;
import com.gresk.modules.promoter.port.PromoterRepository;
import com.gresk.modules.promoter.valueObjects.Email;
import com.gresk.modules.promoter.valueObjects.PromoterId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RegisterPromoterUseCaseTest {

    @Mock
    private PromoterRepository promoterRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private RegisterPromoterUseCase registerPromoterUseCase;

    private RegisterPromoterCommand baseCommand;

    @BeforeEach
    void setUp() {
        baseCommand = new RegisterPromoterCommand(
                "promoter@gresk.com",
                "securePassword123",
                "Club Nocturno",
                "Madrid",
                "España",
                "Calle Gran Vía 1",
                "Club de música electrónica",
                List.of("ROCK", "TECHNO")
        );
    }

    // --- execute() happy path ---

    @Test
    void execute_shouldReturnPromoterIdWhenRegistrationIsSuccessful() {
        when(promoterRepository.existsByEmail(any(Email.class))).thenReturn(Mono.just(false));
        when(passwordEncoder.encode(anyString())).thenReturn("$2a$10$hashedPassword");
        when(promoterRepository.save(any(Promoter.class))).thenAnswer(inv -> Mono.just(inv.getArgument(0)));

        StepVerifier.create(registerPromoterUseCase.execute(baseCommand))
                .assertNext(id -> assertThat(id).isNotNull())
                .verifyComplete();
    }

    @Test
    void execute_shouldSavePromoterWithCorrectEmail() {
        when(promoterRepository.existsByEmail(any(Email.class))).thenReturn(Mono.just(false));
        when(passwordEncoder.encode(anyString())).thenReturn("$2a$10$hashedPassword");
        when(promoterRepository.save(any(Promoter.class))).thenAnswer(inv -> Mono.just(inv.getArgument(0)));

        StepVerifier.create(registerPromoterUseCase.execute(baseCommand))
                .assertNext(id -> assertThat(id).isInstanceOf(PromoterId.class))
                .verifyComplete();

        verify(promoterRepository).save(argThat(p ->
                p.getEmail().value().equals("promoter@gresk.com")));
    }

    @Test
    void execute_shouldSavePromoterWithHashedPassword() {
        when(promoterRepository.existsByEmail(any(Email.class))).thenReturn(Mono.just(false));
        when(passwordEncoder.encode("securePassword123")).thenReturn("$2a$10$hashedPassword");
        when(promoterRepository.save(any(Promoter.class))).thenAnswer(inv -> Mono.just(inv.getArgument(0)));

        StepVerifier.create(registerPromoterUseCase.execute(baseCommand))
                .assertNext(id -> assertThat(id).isNotNull())
                .verifyComplete();

        verify(promoterRepository).save(argThat(p ->
                p.getPassword().hashedValue().equals("$2a$10$hashedPassword")));
    }

    @Test
    void execute_shouldSavePromoterWithCorrectLocation() {
        when(promoterRepository.existsByEmail(any(Email.class))).thenReturn(Mono.just(false));
        when(passwordEncoder.encode(anyString())).thenReturn("$2a$10$hashedPassword");
        when(promoterRepository.save(any(Promoter.class))).thenAnswer(inv -> Mono.just(inv.getArgument(0)));

        StepVerifier.create(registerPromoterUseCase.execute(baseCommand))
                .assertNext(id -> assertThat(id).isNotNull())
                .verifyComplete();

        verify(promoterRepository).save(argThat(p -> {
            assertThat(p.getLocation().city()).isEqualTo("Madrid");
            assertThat(p.getLocation().country()).isEqualTo("España");
            assertThat(p.getLocation().address()).isEqualTo("Calle Gran Vía 1");
            return true;
        }));
    }

    @Test
    void execute_shouldSavePromoterWithDescription() {
        when(promoterRepository.existsByEmail(any(Email.class))).thenReturn(Mono.just(false));
        when(passwordEncoder.encode(anyString())).thenReturn("$2a$10$hashedPassword");
        when(promoterRepository.save(any(Promoter.class))).thenAnswer(inv -> Mono.just(inv.getArgument(0)));

        StepVerifier.create(registerPromoterUseCase.execute(baseCommand))
                .assertNext(id -> assertThat(id).isNotNull())
                .verifyComplete();

        verify(promoterRepository).save(argThat(p ->
                p.getDescription().value().equals("Club de música electrónica")));
    }

    @Test
    void execute_shouldSavePromoterWithMusicalGenres() {
        when(promoterRepository.existsByEmail(any(Email.class))).thenReturn(Mono.just(false));
        when(passwordEncoder.encode(anyString())).thenReturn("$2a$10$hashedPassword");
        when(promoterRepository.save(any(Promoter.class))).thenAnswer(inv -> Mono.just(inv.getArgument(0)));

        StepVerifier.create(registerPromoterUseCase.execute(baseCommand))
                .assertNext(id -> assertThat(id).isNotNull())
                .verifyComplete();

        verify(promoterRepository).save(argThat(p ->
                p.getMusicalGenres().containsAll(List.of(MusicGenre.ROCK, MusicGenre.TECHNO))));
    }

    @Test
    void execute_shouldSavePromoterWithNoGenresWhenMusicalGenresIsNull() {
        RegisterPromoterCommand commandWithoutGenres = new RegisterPromoterCommand(
                "promoter@gresk.com", "pass", "Club", "Madrid", "España", null, null, null);

        when(promoterRepository.existsByEmail(any(Email.class))).thenReturn(Mono.just(false));
        when(passwordEncoder.encode(anyString())).thenReturn("$2a$10$hashedPassword");
        when(promoterRepository.save(any(Promoter.class))).thenAnswer(inv -> Mono.just(inv.getArgument(0)));

        StepVerifier.create(registerPromoterUseCase.execute(commandWithoutGenres))
                .assertNext(id -> assertThat(id).isNotNull())
                .verifyComplete();

        verify(promoterRepository).save(argThat(p -> p.getMusicalGenres().isEmpty()));
    }

    @Test
    void execute_shouldEncodePasswordBeforeSaving() {
        when(promoterRepository.existsByEmail(any(Email.class))).thenReturn(Mono.just(false));
        when(passwordEncoder.encode(anyString())).thenReturn("$2a$10$hashedPassword");
        when(promoterRepository.save(any(Promoter.class))).thenAnswer(inv -> Mono.just(inv.getArgument(0)));

        StepVerifier.create(registerPromoterUseCase.execute(baseCommand))
                .assertNext(id -> assertThat(id).isNotNull())
                .verifyComplete();

        verify(passwordEncoder).encode("securePassword123");
    }

    // --- execute() error cases ---

    @Test
    void execute_shouldThrowEmailAlreadyExistsExceptionWhenEmailIsAlreadyRegistered() {
        when(promoterRepository.existsByEmail(any(Email.class))).thenReturn(Mono.just(true));

        StepVerifier.create(registerPromoterUseCase.execute(baseCommand))
                .expectErrorMatches(ex ->
                        ex instanceof EmailAlreadyExistsException &&
                        ex.getMessage().equals("promoter@gresk.com"))
                .verify();
    }

    @Test
    void execute_shouldNotSaveWhenEmailAlreadyExists() {
        when(promoterRepository.existsByEmail(any(Email.class))).thenReturn(Mono.just(true));

        StepVerifier.create(registerPromoterUseCase.execute(baseCommand))
                .expectError(EmailAlreadyExistsException.class)
                .verify();

        verify(promoterRepository, never()).save(any());
        verify(passwordEncoder, never()).encode(anyString());
    }

    @Test
    void execute_shouldPropagateErrorWhenRepositorySaveFails() {
        when(promoterRepository.existsByEmail(any(Email.class))).thenReturn(Mono.just(false));
        when(passwordEncoder.encode(anyString())).thenReturn("$2a$10$hashedPassword");
        when(promoterRepository.save(any(Promoter.class))).thenReturn(Mono.error(new RuntimeException("DB error")));

        StepVerifier.create(registerPromoterUseCase.execute(baseCommand))
                .expectErrorMessage("DB error")
                .verify();
    }

    @Test
    void execute_shouldThrowWhenEmailFormatIsInvalid() {
        RegisterPromoterCommand invalidCommand = new RegisterPromoterCommand(
                "not-an-email", "pass", "Club", "Madrid", "España", null, null, null);

        StepVerifier.create(registerPromoterUseCase.execute(invalidCommand))
                .expectError(IllegalArgumentException.class)
                .verify();

        verify(promoterRepository, never()).existsByEmail(any());
    }
}
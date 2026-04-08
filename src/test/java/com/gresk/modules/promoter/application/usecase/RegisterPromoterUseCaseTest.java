package com.gresk.modules.promoter.application.usecase;

import com.gresk.modules.promoter.application.command.RegisterPromoterCommand;
import com.gresk.modules.promoter.domain.MusicGenre;
import com.gresk.modules.promoter.domain.exception.EmailAlreadyExistsException;
import com.gresk.modules.promoter.domain.model.Promoter;
import com.gresk.modules.promoter.domain.valueobject.Email;
import com.gresk.modules.promoter.domain.model.valueobject.PromoterId;
import com.gresk.modules.promoter.application.port.out.PasswordHasher;
import com.gresk.modules.promoter.domain.port.out.PromoterRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RegisterPromoterUseCaseTest {

    @Mock private PromoterRepositoryPort promoterRepository;
    @Mock private PasswordHasher passwordHasher;
    @InjectMocks private RegisterPromoterUseCase registerPromoterUseCase;

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
        when(promoterRepository.existsByEmail(any(Email.class))).thenReturn(false);
        when(passwordHasher.hash(anyString())).thenReturn("$2a$10$hashedPassword");
        when(promoterRepository.save(any(Promoter.class))).thenAnswer(inv -> inv.getArgument(0));

        PromoterId id = registerPromoterUseCase.execute(baseCommand);

        assertThat(id).isNotNull();
    }

    @Test
    void execute_shouldSavePromoterWithCorrectEmail() {
        when(promoterRepository.existsByEmail(any(Email.class))).thenReturn(false);
        when(passwordHasher.hash(anyString())).thenReturn("$2a$10$hashedPassword");
        when(promoterRepository.save(any(Promoter.class))).thenAnswer(inv -> inv.getArgument(0));

        PromoterId id = registerPromoterUseCase.execute(baseCommand);

        assertThat(id).isInstanceOf(PromoterId.class);
        verify(promoterRepository).save(argThat(p ->
                p.getEmail().value().equals("promoter@gresk.com")));
    }

    @Test
    void execute_shouldSavePromoterWithHashedPassword() {
        when(promoterRepository.existsByEmail(any(Email.class))).thenReturn(false);
        when(passwordHasher.hash("securePassword123")).thenReturn("$2a$10$hashedPassword");
        when(promoterRepository.save(any(Promoter.class))).thenAnswer(inv -> inv.getArgument(0));

        registerPromoterUseCase.execute(baseCommand);

        verify(promoterRepository).save(argThat(p ->
                p.getPassword().hashedValue().equals("$2a$10$hashedPassword")));
    }

    @Test
    void execute_shouldSavePromoterWithCorrectLocation() {
        when(promoterRepository.existsByEmail(any(Email.class))).thenReturn(false);
        when(passwordHasher.hash(anyString())).thenReturn("$2a$10$hashedPassword");
        when(promoterRepository.save(any(Promoter.class))).thenAnswer(inv -> inv.getArgument(0));

        registerPromoterUseCase.execute(baseCommand);

        verify(promoterRepository).save(argThat(p -> {
            assertThat(p.getLocation().city()).isEqualTo("Madrid");
            assertThat(p.getLocation().country()).isEqualTo("España");
            assertThat(p.getLocation().address()).isEqualTo("Calle Gran Vía 1");
            return true;
        }));
    }

    @Test
    void execute_shouldSavePromoterWithDescription() {
        when(promoterRepository.existsByEmail(any(Email.class))).thenReturn(false);
        when(passwordHasher.hash(anyString())).thenReturn("$2a$10$hashedPassword");
        when(promoterRepository.save(any(Promoter.class))).thenAnswer(inv -> inv.getArgument(0));

        registerPromoterUseCase.execute(baseCommand);

        verify(promoterRepository).save(argThat(p ->
                p.getDescription().value().equals("Club de música electrónica")));
    }

    @Test
    void execute_shouldSavePromoterWithMusicalGenres() {
        when(promoterRepository.existsByEmail(any(Email.class))).thenReturn(false);
        when(passwordHasher.hash(anyString())).thenReturn("$2a$10$hashedPassword");
        when(promoterRepository.save(any(Promoter.class))).thenAnswer(inv -> inv.getArgument(0));

        registerPromoterUseCase.execute(baseCommand);

        verify(promoterRepository).save(argThat(p ->
                p.getMusicalGenres().containsAll(List.of(MusicGenre.ROCK, MusicGenre.TECHNO))));
    }

    @Test
    void execute_shouldSavePromoterWithNoGenresWhenMusicalGenresIsNull() {
        RegisterPromoterCommand commandWithoutGenres = new RegisterPromoterCommand(
                "promoter@gresk.com", "pass", "Club", "Madrid", "España", null, null, null);

        when(promoterRepository.existsByEmail(any(Email.class))).thenReturn(false);
        when(passwordHasher.hash(anyString())).thenReturn("$2a$10$hashedPassword");
        when(promoterRepository.save(any(Promoter.class))).thenAnswer(inv -> inv.getArgument(0));

        registerPromoterUseCase.execute(commandWithoutGenres);

        verify(promoterRepository).save(argThat(p -> p.getMusicalGenres().isEmpty()));
    }

    @Test
    void execute_shouldEncodePasswordBeforeSaving() {
        when(promoterRepository.existsByEmail(any(Email.class))).thenReturn(false);
        when(passwordHasher.hash(anyString())).thenReturn("$2a$10$hashedPassword");
        when(promoterRepository.save(any(Promoter.class))).thenAnswer(inv -> inv.getArgument(0));

        registerPromoterUseCase.execute(baseCommand);

        verify(passwordHasher).hash("securePassword123");
    }

    // --- execute() error cases ---

    @Test
    void execute_shouldThrowEmailAlreadyExistsExceptionWhenEmailIsAlreadyRegistered() {
        when(promoterRepository.existsByEmail(any(Email.class))).thenReturn(true);

        EmailAlreadyExistsException ex = assertThrows(
                EmailAlreadyExistsException.class,
                () -> registerPromoterUseCase.execute(baseCommand)
        );
        assertThat(ex.getMessage()).isEqualTo("promoter@gresk.com");
    }

    @Test
    void execute_shouldNotSaveWhenEmailAlreadyExists() {
        when(promoterRepository.existsByEmail(any(Email.class))).thenReturn(true);

        assertThrows(EmailAlreadyExistsException.class,
                () -> registerPromoterUseCase.execute(baseCommand));

        verify(promoterRepository, never()).save(any());
        verify(passwordHasher, never()).hash(anyString());
    }

    @Test
    void execute_shouldPropagateErrorWhenRepositorySaveFails() {
        when(promoterRepository.existsByEmail(any(Email.class))).thenReturn(false);
        when(passwordHasher.hash(anyString())).thenReturn("$2a$10$hashedPassword");
        when(promoterRepository.save(any(Promoter.class))).thenThrow(new RuntimeException("DB error"));

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> registerPromoterUseCase.execute(baseCommand));
        assertThat(ex.getMessage()).isEqualTo("DB error");
    }

    @Test
    void execute_shouldThrowWhenEmailFormatIsInvalid() {
        RegisterPromoterCommand invalidCommand = new RegisterPromoterCommand(
                "not-an-email", "pass", "Club", "Madrid", "España", null, null, null);

        assertThrows(IllegalArgumentException.class,
                () -> registerPromoterUseCase.execute(invalidCommand));

        verify(promoterRepository, never()).existsByEmail(any());
    }
}

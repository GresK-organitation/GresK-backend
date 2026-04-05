package com.gresk.modules.promoter.application.usecase;

import com.gresk.modules.promoter.application.command.UpdatePromoterProfileCommand;
import com.gresk.modules.promoter.domain.PromoterStatus;
import com.gresk.modules.promoter.domain.exception.InvalidGenreException;
import com.gresk.modules.promoter.domain.exception.PromoterNotFoundException;
import com.gresk.modules.promoter.domain.model.Promoter;
import com.gresk.modules.promoter.domain.valueobject.*;
import com.gresk.modules.promoter.domain.port.out.PromoterRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UpdatePromoterProfileUseCaseTest {

    @Mock private PromoterRepository promoterRepository;
    @InjectMocks private UpdatePromoterProfileUseCase useCase;

    private Promoter buildPromoter() {
        return Promoter.reconstitute(
                PromoterId.generate(),
                new Email("promoter@gresk.com"),
                new Password("$2a$10$hash"),
                new PromoterName("Club Nocturno"),
                new Description("Old description"),
                new Location("Madrid", "España", null),
                Set.of(),
                PromoterStatus.ACTIVE,
                LocalDateTime.now(),
                true
        );
    }

    @Test
    void execute_shouldUpdateSuccessfully() {
        Promoter promoter = buildPromoter();
        String id = promoter.getId().value().toString();
        when(promoterRepository.findById(any(PromoterId.class))).thenReturn(Optional.of(promoter));
        when(promoterRepository.save(any(Promoter.class))).thenAnswer(inv -> inv.getArgument(0));

        UpdatePromoterProfileCommand command = new UpdatePromoterProfileCommand(
                id, "Nuevo Nombre", "Barcelona", "España", null, "New description",
                Set.of("ROCK", "JAZZ")
        );

        assertDoesNotThrow(() -> useCase.execute(command));
    }

    @Test
    void execute_shouldThrowPromoterNotFoundExceptionWhenMissing() {
        when(promoterRepository.findById(any(PromoterId.class))).thenReturn(Optional.empty());
        String id = PromoterId.generate().value().toString();

        UpdatePromoterProfileCommand command = new UpdatePromoterProfileCommand(
                id, null, null, null, null, null, null
        );

        assertThrows(PromoterNotFoundException.class, () -> useCase.execute(command));
    }

    @Test
    void execute_shouldThrowInvalidGenreExceptionForBadGenre() {
        Promoter promoter = buildPromoter();
        String id = promoter.getId().value().toString();
        when(promoterRepository.findById(any(PromoterId.class))).thenReturn(Optional.of(promoter));

        UpdatePromoterProfileCommand command = new UpdatePromoterProfileCommand(
                id, null, null, null, null, null, Set.of("ROCK", "NOT_A_GENRE")
        );

        assertThrows(InvalidGenreException.class, () -> useCase.execute(command));
    }
}

package com.gresk.modules.promoter.application.usecase;

import com.gresk.modules.promoter.application.command.UpdatePromoterProfileCommand;
import com.gresk.modules.promoter.domain.PromoterStatus;
import com.gresk.modules.promoter.domain.exception.InvalidGenreException;
import com.gresk.modules.promoter.domain.exception.PromoterNotFoundException;
import com.gresk.modules.promoter.domain.model.Promoter;
import com.gresk.modules.promoter.domain.valueobject.*;
import com.gresk.modules.promoter.port.PromoterRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;
import java.util.Set;

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

    // Orden de campos del command: promoterId, name, city, country, address, description, musicalGenres

    @Test
    void execute_shouldUpdateAndCompleteSuccessfully() {
        Promoter promoter = buildPromoter();
        String id = promoter.getId().value().toString();
        when(promoterRepository.findById(any(PromoterId.class))).thenReturn(Mono.just(promoter));
        when(promoterRepository.save(any(Promoter.class))).thenReturn(Mono.just(promoter));

        UpdatePromoterProfileCommand command = new UpdatePromoterProfileCommand(
                id, "Nuevo Nombre", "Barcelona", "España", null, "New description",
                Set.of("ROCK", "JAZZ")
        );

        StepVerifier.create(useCase.execute(command))
                .verifyComplete();
    }

    @Test
    void execute_shouldEmitPromoterNotFoundExceptionWhenMissing() {
        when(promoterRepository.findById(any(PromoterId.class))).thenReturn(Mono.empty());
        String id = PromoterId.generate().value().toString();

        UpdatePromoterProfileCommand command = new UpdatePromoterProfileCommand(
                id, null, null, null, null, null, null
        );

        StepVerifier.create(useCase.execute(command))
                .expectError(PromoterNotFoundException.class)
                .verify();
    }

    @Test
    void execute_shouldEmitInvalidGenreExceptionForBadGenre() {
        Promoter promoter = buildPromoter();
        String id = promoter.getId().value().toString();
        when(promoterRepository.findById(any(PromoterId.class))).thenReturn(Mono.just(promoter));

        UpdatePromoterProfileCommand command = new UpdatePromoterProfileCommand(
                id, null, null, null, null, null, Set.of("ROCK", "NOT_A_GENRE")
        );

        StepVerifier.create(useCase.execute(command))
                .expectError(InvalidGenreException.class)
                .verify();
    }
}

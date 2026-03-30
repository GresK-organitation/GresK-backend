package com.gresk.modules.promoter.application.usecase;

import com.gresk.modules.promoter.application.query.GetPromoterQuery;
import com.gresk.modules.promoter.domain.PromoterStatus;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetPromoterUseCaseTest {

    @Mock private PromoterRepository promoterRepository;
    @InjectMocks private GetPromoterUseCase useCase;

    private Promoter buildPromoter() {
        return Promoter.reconstitute(
                PromoterId.generate(),
                new Email("promoter@gresk.com"),
                new Password("$2a$10$hash"),
                new PromoterName("Club Nocturno"),
                new Description("A club"),
                new Location("Madrid", "España", null),
                Set.of(),
                PromoterStatus.ACTIVE,
                LocalDateTime.now(),
                true
        );
    }

    @Test
    void execute_shouldEmitPromoterWhenFound() {
        Promoter promoter = buildPromoter();
        String id = promoter.getId().value().toString();
        when(promoterRepository.findById(any(PromoterId.class))).thenReturn(Mono.just(promoter));

        StepVerifier.create(useCase.execute(new GetPromoterQuery(id)))
                .assertNext(p -> assertThat(p).isNotNull())
                .verifyComplete();
    }

    @Test
    void execute_shouldEmitPromoterNotFoundExceptionWhenMissing() {
        when(promoterRepository.findById(any(PromoterId.class))).thenReturn(Mono.empty());
        String id = PromoterId.generate().value().toString();

        StepVerifier.create(useCase.execute(new GetPromoterQuery(id)))
                .expectError(PromoterNotFoundException.class)
                .verify();
    }
}

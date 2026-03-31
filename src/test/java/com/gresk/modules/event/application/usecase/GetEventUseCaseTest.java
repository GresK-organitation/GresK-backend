package com.gresk.modules.event.application.usecase;

import com.gresk.modules.event.application.query.GetEventQuery;
import com.gresk.modules.event.domain.exception.EventNotFoundException;
import com.gresk.modules.event.domain.model.Event;
import com.gresk.modules.event.domain.model.EventId;
import com.gresk.modules.event.domain.port.out.EventRepository;
import com.gresk.modules.promoter.domain.valueobject.PromoterId;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetEventUseCaseTest {

    @Mock
    private EventRepository eventRepository;

    @InjectMocks
    private GetEventUseCase useCase;

    @Test
    void execute_shouldReturnEventWhenFound() {
        Event event = Event.create("Summer Fest", PromoterId.generate());
        String eventId = event.getId().toString();
        when(eventRepository.findById(any(EventId.class))).thenReturn(Mono.just(event));

        StepVerifier.create(useCase.execute(new GetEventQuery(eventId)))
                .assertNext(e -> assertThat(e).isNotNull())
                .verifyComplete();
    }

    @Test
    void execute_shouldThrowEventNotFoundExceptionWhenMissing() {
        String eventId = EventId.generate().toString();
        when(eventRepository.findById(any(EventId.class))).thenReturn(Mono.empty());

        StepVerifier.create(useCase.execute(new GetEventQuery(eventId)))
                .expectError(EventNotFoundException.class)
                .verify();
    }

    @Test
    void execute_shouldThrowWhenEventIdIsInvalid() {
        StepVerifier.create(useCase.execute(new GetEventQuery("not-a-uuid")))
                .expectError(IllegalArgumentException.class)
                .verify();
    }
}

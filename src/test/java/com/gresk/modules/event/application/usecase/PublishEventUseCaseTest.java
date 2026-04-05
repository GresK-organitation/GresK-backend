package com.gresk.modules.event.application.usecase;

import com.gresk.modules.event.domain.exception.EventNotFoundException;
import com.gresk.modules.event.domain.exception.ForbiddenOperationException;
import com.gresk.modules.event.domain.exception.IncompleteEventException;
import com.gresk.modules.event.domain.model.*;
import com.gresk.modules.event.domain.port.out.EventRepository;
import com.gresk.modules.promoter.domain.valueobject.PromoterId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PublishEventUseCaseTest {

    @Mock
    private EventRepository eventRepository;

    @InjectMocks
    private PublishEventUseCase useCase;

    private PromoterId ownerId;
    private String ownerIdStr;
    private String eventIdStr;

    @BeforeEach
    void setUp() {
        ownerId = PromoterId.generate();
        ownerIdStr = ownerId.toString();
        eventIdStr = EventId.generate().toString();
    }

    private Event completeEvent(PromoterId promoterId) {
        return Event.create("Summer Fest", promoterId)
                .withGenre(Genre.ELECTRONIC)
                .withPrice(new Price(new BigDecimal("20.00"), "EUR"))
                .withCapacity(Capacity.of(500))
                .withEventDate(LocalDateTime.now().plusMonths(1));
    }

    // --- happy path ---

    @Test
    void execute_shouldPublishEventAndReturnEvent() {
        Event event = completeEvent(ownerId);
        when(eventRepository.findById(any(EventId.class))).thenReturn(Optional.of(event));
        when(eventRepository.save(any(Event.class))).thenAnswer(inv -> inv.getArgument(0));

        Event result = useCase.execute(eventIdStr, ownerIdStr);
        assertThat(result.getStatus()).isEqualTo(EventStatus.PUBLISHED);

        verify(eventRepository).save(argThat(e -> e.getStatus() == EventStatus.PUBLISHED));
    }

    // --- not found ---

    @Test
    void execute_shouldThrowEventNotFoundExceptionWhenEventDoesNotExist() {
        when(eventRepository.findById(any(EventId.class))).thenReturn(Optional.empty());

        assertThrows(EventNotFoundException.class,
                () -> useCase.execute(eventIdStr, ownerIdStr));

        verify(eventRepository, never()).save(any());
    }

    // --- forbidden ---

    @Test
    void execute_shouldThrowForbiddenOperationExceptionWhenRequesterIsNotOwner() {
        PromoterId differentOwner = PromoterId.generate();
        Event event = completeEvent(differentOwner);
        when(eventRepository.findById(any(EventId.class))).thenReturn(Optional.of(event));

        assertThrows(ForbiddenOperationException.class,
                () -> useCase.execute(eventIdStr, ownerIdStr));

        verify(eventRepository, never()).save(any());
    }

    // --- domain exception propagation ---

    @Test
    void execute_shouldPropagateIncompleteEventExceptionFromDomain() {
        Event incompleteEvent = Event.create("Summer Fest", ownerId);
        when(eventRepository.findById(any(EventId.class))).thenReturn(Optional.of(incompleteEvent));

        assertThrows(IncompleteEventException.class,
                () -> useCase.execute(eventIdStr, ownerIdStr));

        verify(eventRepository, never()).save(any());
    }
}

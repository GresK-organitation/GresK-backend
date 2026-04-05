package com.gresk.modules.event.application.usecase;

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

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateEventUseCaseTest {

    @Mock
    private EventRepository eventRepository;

    @InjectMocks
    private CreateEventUseCase useCase;

    private String promoterId;
    private CreateEventCommand minimalCommand;

    @BeforeEach
    void setUp() {
        promoterId = PromoterId.generate().toString();
        minimalCommand = new CreateEventCommand(
                promoterId, "Summer Fest",
                null, null, null, null, null, null, null, null, null
        );
    }

    // --- execute() happy path ---

    @Test
    void execute_shouldReturnEventOnSuccess() {
        when(eventRepository.save(any(Event.class))).thenAnswer(inv -> inv.getArgument(0));

        Event result = useCase.execute(minimalCommand);
        assertThat(result).isNotNull();

        verify(eventRepository).save(any(Event.class));
    }

    @Test
    void execute_shouldBuildEventWithOptionalFields() {
        when(eventRepository.save(any(Event.class))).thenAnswer(inv -> inv.getArgument(0));
        LocalDateTime eventDate = LocalDateTime.now().plusMonths(1);
        CreateEventCommand fullCommand = new CreateEventCommand(
                promoterId, "Summer Fest",
                "ELECTRONIC",
                new BigDecimal("25.00"), "EUR",
                300,
                eventDate,
                "Madrid", "Calle Gran Vía 1", "Sala Riviera",
                null
        );

        Event result = useCase.execute(fullCommand);
        assertThat(result).isNotNull();

        verify(eventRepository).save(argThat(event -> {
            assertThat(event.getGenre()).isEqualTo(Genre.ELECTRONIC);
            assertThat(event.getPrice().amount()).isEqualByComparingTo("25.00");
            assertThat(event.getCapacity().total()).isEqualTo(300);
            assertThat(event.getEventDate()).isEqualTo(eventDate);
            assertThat(event.getLocation().city()).isEqualTo("Madrid");
            return true;
        }));
    }

    @Test
    void execute_shouldSaveEventInDraftStatus() {
        when(eventRepository.save(any(Event.class))).thenAnswer(inv -> inv.getArgument(0));

        useCase.execute(minimalCommand);

        verify(eventRepository).save(argThat(event ->
                event.getStatus() == EventStatus.DRAFT));
    }

    // --- execute() error cases ---

    @Test
    void execute_shouldPropagateErrorWhenRepositoryFails() {
        when(eventRepository.save(any(Event.class)))
                .thenThrow(new RuntimeException("DB error"));

        assertThrows(RuntimeException.class, () -> useCase.execute(minimalCommand));
    }

    @Test
    void execute_shouldThrowWhenPromoterIdIsInvalid() {
        CreateEventCommand badCommand = new CreateEventCommand(
                "not-a-uuid", "Summer Fest",
                null, null, null, null, null, null, null, null, null
        );

        assertThrows(IllegalArgumentException.class, () -> useCase.execute(badCommand));

        verify(eventRepository, never()).save(any());
    }
}

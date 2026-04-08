package com.gresk.modules.event.application.usecase;

import com.gresk.modules.event.domain.model.Event;
import com.gresk.modules.event.domain.port.out.EventFilter;
import com.gresk.modules.event.domain.port.out.EventRepository;
import com.gresk.modules.promoter.domain.model.valueobject.PromoterId;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ListEventsUseCaseTest {

    @Mock
    private EventRepository eventRepository;

    @InjectMocks
    private ListEventsUseCase useCase;

    private final EventFilter filter = EventFilter.empty();
    private final PageRequest pageRequest = PageRequest.of(0, 10);

    // --- execute() ---

    @Test
    void execute_shouldReturnAllEventsFromRepository() {
        Event event1 = Event.create("Summer Fest", PromoterId.generate());
        Event event2 = Event.create("Winter Bash", PromoterId.generate());
        when(eventRepository.findAll(eq(filter), eq(pageRequest)))
                .thenReturn(List.of(event1, event2));

        List<Event> result = useCase.execute(filter, pageRequest);
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getTitle()).isEqualTo("Summer Fest");
        assertThat(result.get(1).getTitle()).isEqualTo("Winter Bash");
    }

    @Test
    void execute_shouldReturnEmptyListWhenNoResults() {
        when(eventRepository.findAll(any(EventFilter.class), any(PageRequest.class)))
                .thenReturn(List.of());

        assertThat(useCase.execute(filter, pageRequest)).isEmpty();
    }

    // --- count() ---

    @Test
    void count_shouldReturnTotalCountFromRepository() {
        when(eventRepository.count(eq(filter))).thenReturn(42L);

        assertThat(useCase.count(filter)).isEqualTo(42L);
    }
}

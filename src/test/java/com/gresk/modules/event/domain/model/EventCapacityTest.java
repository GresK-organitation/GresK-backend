package com.gresk.modules.event.domain.model;

import com.gresk.modules.event.domain.exception.EventNotPublishedException;
import com.gresk.modules.event.domain.exception.EventSoldOutException;
import com.gresk.modules.promoter.domain.model.valueobject.PromoterId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class EventCapacityTest {

    private PromoterId promoterId;

    @BeforeEach
    void setUp() {
        promoterId = PromoterId.generate();
    }

    @Test
    void decrementCapacity_reducesAvailableByOne() {
        Event event = publishedEvent(10);

        event.decrementCapacity();

        assertThat(event.getCapacity().available()).isEqualTo(9);
    }

    @Test
    void decrementCapacity_atZeroCapacity_throwsEventSoldOutException() {
        Event event = publishedEvent(1);
        event.decrementCapacity();

        assertThatThrownBy(event::decrementCapacity)
                .isInstanceOf(EventSoldOutException.class);
    }

    @Test
    void decrementCapacity_onDraftEvent_throwsEventNotPublishedException() {
        Event event = Event.create("Draft Event", promoterId)
                .withGenre(Genre.ELECTRONIC)
                .withPrice(new Price(new BigDecimal("20.00"), "EUR"))
                .withCapacity(Capacity.of(100))
                .withEventDate(LocalDateTime.now().plusMonths(1))
                .withLocation(new Location("Madrid", "Calle Gran Vía 1", "Sala Riviera"));

        assertThatThrownBy(event::decrementCapacity)
                .isInstanceOf(EventNotPublishedException.class);
    }

    @Test
    void hasAvailableCapacity_returnsTrueWhenAvailableGreaterThanZero() {
        Event event = publishedEvent(5);

        assertThat(event.hasAvailableCapacity()).isTrue();
    }

    @Test
    void hasAvailableCapacity_returnsFalseWhenAvailableIsZero() {
        Event event = publishedEvent(1);
        event.decrementCapacity();

        assertThat(event.hasAvailableCapacity()).isFalse();
    }

    private Event publishedEvent(int capacity) {
        Event event = Event.create("Test Event", promoterId)
                .withGenre(Genre.ELECTRONIC)
                .withPrice(new Price(new BigDecimal("20.00"), "EUR"))
                .withCapacity(Capacity.of(capacity))
                .withEventDate(LocalDateTime.now().plusMonths(1))
                .withLocation(new Location("Madrid", "Calle Gran Vía 1", "Sala Riviera"));
        event.publish();
        return event;
    }
}

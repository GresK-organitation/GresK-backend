package com.gresk.modules.event.domain.model;

import com.gresk.modules.event.domain.exception.IncompleteEventException;
import com.gresk.modules.event.domain.exception.InvalidEventTransitionException;
import com.gresk.modules.promoter.domain.valueobject.PromoterId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

class EventTest {

    private PromoterId promoterId;
    private Genre genre;
    private Price price;
    private Capacity capacity;
    private LocalDateTime eventDate;
    private Location location;

    @BeforeEach
    void setUp() {
        promoterId = PromoterId.generate();
        genre = Genre.ELECTRONIC;
        price = new Price(new BigDecimal("20.00"), "EUR");
        capacity = Capacity.of(500);
        eventDate = LocalDateTime.now().plusMonths(1);
        location = new Location("Madrid", "Calle Gran Vía 1", "Sala Riviera");
    }

    // --- create() ---

    @Test
    void create_shouldSetStatusToDraft() {
        Event event = Event.create("Summer Fest", promoterId);
        assertThat(event.getStatus()).isEqualTo(EventStatus.DRAFT);
    }

    @Test
    void create_shouldGenerateId() {
        Event event = Event.create("Summer Fest", promoterId);
        assertThat(event.getId()).isNotNull();
    }

    @Test
    void create_shouldSetCreatedAt() {
        LocalDateTime before = LocalDateTime.now().minusSeconds(1);
        Event event = Event.create("Summer Fest", promoterId);
        assertThat(event.getCreatedAt()).isAfter(before);
        assertThat(event.getCreatedAt()).isBefore(LocalDateTime.now().plusSeconds(1));
    }

    @Test
    void create_shouldStoreTitleAndPromoterId() {
        Event event = Event.create("Summer Fest", promoterId);
        assertThat(event.getTitle()).isEqualTo("Summer Fest");
        assertThat(event.getPromoterId()).isEqualTo(promoterId);
    }

    @Test
    void create_shouldLeaveOptionalFieldsNull() {
        Event event = Event.create("Summer Fest", promoterId);
        assertThat(event.getGenre()).isNull();
        assertThat(event.getPrice()).isNull();
        assertThat(event.getCapacity()).isNull();
        assertThat(event.getEventDate()).isNull();
        assertThat(event.getLocation()).isNull();
        assertThat(event.getRevealAt()).isNull();
    }

    // --- reconstitute() ---

    @Test
    void reconstitute_shouldRestoreAllFields() {
        EventId id = EventId.generate();
        LocalDateTime createdAt = LocalDateTime.of(2025, 6, 1, 10, 0);
        LocalDateTime revealAt = LocalDateTime.of(2025, 7, 1, 20, 0);

        Event event = Event.reconstitute(id, "Summer Fest", promoterId, genre, price,
                capacity, eventDate, location, revealAt, EventStatus.PUBLISHED, createdAt);

        assertThat(event.getId()).isEqualTo(id);
        assertThat(event.getTitle()).isEqualTo("Summer Fest");
        assertThat(event.getPromoterId()).isEqualTo(promoterId);
        assertThat(event.getGenre()).isEqualTo(genre);
        assertThat(event.getPrice()).isEqualTo(price);
        assertThat(event.getCapacity()).isEqualTo(capacity);
        assertThat(event.getEventDate()).isEqualTo(eventDate);
        assertThat(event.getLocation()).isEqualTo(location);
        assertThat(event.getRevealAt()).isEqualTo(revealAt);
        assertThat(event.getStatus()).isEqualTo(EventStatus.PUBLISHED);
        assertThat(event.getCreatedAt()).isEqualTo(createdAt);
    }

    // --- publish() ---

    @Test
    void publish_shouldSetStatusToPublished() {
        Event event = completeEvent();
        event.publish();
        assertThat(event.getStatus()).isEqualTo(EventStatus.PUBLISHED);
    }

    @Test
    void publish_shouldThrowIncompleteEventExceptionWhenGenreIsNull() {
        Event event = completeEvent().withGenre(null);
        assertThatExceptionOfType(IncompleteEventException.class)
                .isThrownBy(event::publish)
                .withMessageContaining("genre");
    }

    @Test
    void publish_shouldThrowIncompleteEventExceptionWhenPriceIsNull() {
        Event event = completeEvent().withPrice(null);
        assertThatExceptionOfType(IncompleteEventException.class)
                .isThrownBy(event::publish)
                .withMessageContaining("price");
    }

    @Test
    void publish_shouldThrowIncompleteEventExceptionWhenCapacityIsNull() {
        Event event = completeEvent().withCapacity(null);
        assertThatExceptionOfType(IncompleteEventException.class)
                .isThrownBy(event::publish)
                .withMessageContaining("capacity");
    }

    @Test
    void publish_shouldThrowIncompleteEventExceptionWhenEventDateIsNull() {
        Event event = completeEvent().withEventDate(null);
        assertThatExceptionOfType(IncompleteEventException.class)
                .isThrownBy(event::publish)
                .withMessageContaining("eventDate");
    }

    @Test
    void publish_shouldThrowInvalidEventTransitionExceptionWhenAlreadyPublished() {
        Event event = completeEvent();
        event.publish();
        assertThatExceptionOfType(InvalidEventTransitionException.class)
                .isThrownBy(event::publish);
    }

    // --- finish() ---

    @Test
    void finish_shouldSetStatusToFinished() {
        Event event = completeEvent();
        event.publish();
        event.finish();
        assertThat(event.getStatus()).isEqualTo(EventStatus.FINISHED);
    }

    @Test
    void finish_shouldThrowInvalidEventTransitionExceptionWhenDraft() {
        Event event = Event.create("Summer Fest", promoterId);
        assertThatExceptionOfType(InvalidEventTransitionException.class)
                .isThrownBy(event::finish);
    }

    // --- cancel() ---

    @Test
    void cancel_shouldSetStatusToCancelledFromDraft() {
        Event event = Event.create("Summer Fest", promoterId);
        event.cancel();
        assertThat(event.getStatus()).isEqualTo(EventStatus.CANCELLED);
    }

    @Test
    void cancel_shouldThrowInvalidEventTransitionExceptionWhenFinished() {
        Event event = completeEvent();
        event.publish();
        event.finish();
        assertThatExceptionOfType(InvalidEventTransitionException.class)
                .isThrownBy(event::cancel);
    }

    // --- helper ---

    private Event completeEvent() {
        return Event.create("Summer Fest", promoterId)
                .withGenre(genre)
                .withPrice(price)
                .withCapacity(capacity)
                .withEventDate(eventDate)
                .withLocation(location);
    }
}

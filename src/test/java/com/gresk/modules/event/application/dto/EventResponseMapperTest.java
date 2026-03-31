package com.gresk.modules.event.application.dto;

import com.gresk.modules.event.domain.model.*;
import com.gresk.modules.promoter.domain.valueobject.PromoterId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class EventResponseMapperTest {

    private EventResponseMapper mapper;
    private PromoterId promoterId;

    @BeforeEach
    void setUp() {
        mapper = new EventResponseMapper();
        promoterId = PromoterId.generate();
    }

    @Test
    void toResponse_shouldMapAllFieldsOfCompleteEvent() {
        LocalDateTime eventDate = LocalDateTime.of(2025, 8, 15, 21, 0);
        LocalDateTime revealAt  = LocalDateTime.of(2025, 8, 10, 0, 0);

        Event event = Event.create("Summer Fest", promoterId)
                .withGenre(Genre.ELECTRONIC)
                .withPrice(new Price(new BigDecimal("25.00"), "EUR"))
                .withCapacity(new Capacity(500, 480))
                .withEventDate(eventDate)
                .withLocation(new Location("Madrid", "Gran Vía 1", "Sala Riviera"))
                .withRevealAt(revealAt);

        EventResponse response = mapper.toResponse(event);

        assertThat(response.id()).isEqualTo(event.getId().toString());
        assertThat(response.title()).isEqualTo("Summer Fest");
        assertThat(response.promoterId()).isEqualTo(promoterId.toString());
        assertThat(response.status()).isEqualTo("DRAFT");
        assertThat(response.genre()).isEqualTo("ELECTRONIC");
        assertThat(response.amount()).isEqualByComparingTo("25.00");
        assertThat(response.currency()).isEqualTo("EUR");
        assertThat(response.totalCapacity()).isEqualTo(500);
        assertThat(response.availableCapacity()).isEqualTo(480);
        assertThat(response.eventDate()).isEqualTo(eventDate);
        assertThat(response.city()).isEqualTo("Madrid");
        assertThat(response.address()).isEqualTo("Gran Vía 1");
        assertThat(response.venue()).isEqualTo("Sala Riviera");
        assertThat(response.revealAt()).isEqualTo(revealAt);
        assertThat(response.createdAt()).isNotNull();
    }

    @Test
    void toResponse_shouldMapNullableFieldsAsNullWhenAbsent() {
        Event event = Event.create("Draft Event", promoterId); // no optional fields set

        EventResponse response = mapper.toResponse(event);

        assertThat(response.id()).isNotNull();
        assertThat(response.title()).isEqualTo("Draft Event");
        assertThat(response.status()).isEqualTo("DRAFT");
        assertThat(response.genre()).isNull();
        assertThat(response.amount()).isNull();
        assertThat(response.currency()).isNull();
        assertThat(response.totalCapacity()).isNull();
        assertThat(response.availableCapacity()).isNull();
        assertThat(response.eventDate()).isNull();
        assertThat(response.city()).isNull();
        assertThat(response.address()).isNull();
        assertThat(response.venue()).isNull();
        assertThat(response.revealAt()).isNull();
    }
}

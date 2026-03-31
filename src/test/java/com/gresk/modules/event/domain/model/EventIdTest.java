package com.gresk.modules.event.domain.model;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class EventIdTest {

    // --- constructor ---

    @Test
    void constructor_shouldCreateEventIdWithValidUuid() {
        UUID uuid = UUID.randomUUID();
        EventId eventId = new EventId(uuid);
        assertThat(eventId.value()).isEqualTo(uuid);
    }

    @Test
    void constructor_shouldThrowWhenValueIsNull() {
        assertThatThrownBy(() -> new EventId(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("EventId value must not be null");
    }

    // --- generate() ---

    @Test
    void generate_shouldReturnNonNullEventId() {
        assertThat(EventId.generate()).isNotNull();
    }

    @Test
    void generate_shouldReturnDistinctIdsOnEachCall() {
        EventId first = EventId.generate();
        EventId second = EventId.generate();
        assertThat(first).isNotEqualTo(second);
    }

    // --- of(String) ---

    @Test
    void of_shouldCreateEventIdFromValidUuidString() {
        String uuidString = UUID.randomUUID().toString();
        EventId eventId = EventId.of(uuidString);
        assertThat(eventId.toString()).isEqualTo(uuidString);
    }

    @Test
    void of_shouldThrowWhenStringIsNotValidUuid() {
        assertThatThrownBy(() -> EventId.of("not-a-uuid"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Invalid EventId format");
    }

    // --- toString() ---

    @Test
    void toString_shouldReturnUuidString() {
        UUID uuid = UUID.randomUUID();
        EventId eventId = new EventId(uuid);
        assertThat(eventId.toString()).isEqualTo(uuid.toString());
    }

    // --- equality ---

    @Test
    void equality_shouldBeBasedOnUuidValue() {
        UUID uuid = UUID.randomUUID();
        assertThat(new EventId(uuid)).isEqualTo(new EventId(uuid));
    }
}

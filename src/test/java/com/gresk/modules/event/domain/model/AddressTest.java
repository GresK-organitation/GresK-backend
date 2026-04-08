package com.gresk.modules.event.domain.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AddressTest {

    // --- constructor (valid) ---

    @Test
    void constructor_shouldCreateLocationWithCityAddressAndVenue() {
        Location location = new Location("Madrid", "Calle Gran Vía 1", "Sala Riviera");
        assertThat(location.city()).isEqualTo("Madrid");
        assertThat(location.address()).isEqualTo("Calle Gran Vía 1");
        assertThat(location.venue()).isEqualTo("Sala Riviera");
    }

    @Test
    void constructor_shouldAllowNullVenue() {
        Location location = new Location("Barcelona", "Av. Diagonal 100", null);
        assertThat(location.venue()).isNull();
    }

    // --- city validation ---

    @Test
    void constructor_shouldThrowWhenCityIsNull() {
        assertThatThrownBy(() -> new Location(null, "Calle Mayor 5", "Venue"))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("Address city must not be null");
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "   ", "\t"})
    void constructor_shouldThrowWhenCityIsBlank(String blankCity) {
        assertThatThrownBy(() -> new Location(blankCity, "Calle Mayor 5", "Venue"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Address city must not be blank");
    }

    // --- address validation ---

    @Test
    void constructor_shouldThrowWhenAddressIsNull() {
        assertThatThrownBy(() -> new Location("Madrid", null, "Venue"))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("Address address must not be null");
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "   ", "\t"})
    void constructor_shouldThrowWhenAddressIsBlank(String blankAddress) {
        assertThatThrownBy(() -> new Location("Madrid", blankAddress, "Venue"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Address address must not be blank");
    }

    // --- equality ---

    @Test
    void equality_shouldBeBasedOnAllFields() {
        Location a = new Location("Madrid", "Calle Gran Vía 1", "Sala Riviera");
        Location b = new Location("Madrid", "Calle Gran Vía 1", "Sala Riviera");
        assertThat(a).isEqualTo(b);
    }
}

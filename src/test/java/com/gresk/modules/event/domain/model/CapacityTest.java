package com.gresk.modules.event.domain.model;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CapacityTest {

    // --- constructor (valid) ---

    @Test
    void constructor_shouldCreateCapacityWithValidTotalAndAvailable() {
        Capacity capacity = new Capacity(100, 80);
        assertThat(capacity.total()).isEqualTo(100);
        assertThat(capacity.available()).isEqualTo(80);
    }

    @Test
    void constructor_shouldAllowAvailableEqualToTotal() {
        Capacity capacity = new Capacity(50, 50);
        assertThat(capacity.available()).isEqualTo(capacity.total());
    }

    @Test
    void constructor_shouldAllowZeroAvailable() {
        Capacity capacity = new Capacity(100, 0);
        assertThat(capacity.available()).isZero();
    }

    // --- total validation ---

    @Test
    void constructor_shouldThrowWhenTotalIsZero() {
        assertThatThrownBy(() -> new Capacity(0, 0))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Capacity total must be at least 1");
    }

    @Test
    void constructor_shouldThrowWhenTotalIsNegative() {
        assertThatThrownBy(() -> new Capacity(-1, 0))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Capacity total must be at least 1");
    }

    // --- available validation ---

    @Test
    void constructor_shouldThrowWhenAvailableIsNegative() {
        assertThatThrownBy(() -> new Capacity(100, -1))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Capacity available must not be negative");
    }

    @Test
    void constructor_shouldThrowWhenAvailableExceedsTotal() {
        assertThatThrownBy(() -> new Capacity(50, 51))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Capacity available must not exceed total");
    }

    // --- of() factory ---

    @Test
    void of_shouldSetAvailableEqualToTotal() {
        Capacity capacity = Capacity.of(200);
        assertThat(capacity.total()).isEqualTo(200);
        assertThat(capacity.available()).isEqualTo(200);
    }

    @Test
    void of_shouldThrowWhenTotalIsLessThanOne() {
        assertThatThrownBy(() -> Capacity.of(0))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Capacity total must be at least 1");
    }

    // --- equality ---

    @Test
    void equality_shouldBeBasedOnTotalAndAvailable() {
        assertThat(new Capacity(100, 80)).isEqualTo(new Capacity(100, 80));
    }
}

package com.gresk.modules.event.domain.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PriceTest {

    // --- constructor (valid) ---

    @Test
    void constructor_shouldCreatePriceWithValidAmountAndCurrency() {
        Price price = new Price(new BigDecimal("10.00"), "EUR");
        assertThat(price.amount()).isEqualByComparingTo("10.00");
        assertThat(price.currency()).isEqualTo("EUR");
    }

    // --- amount validation ---

    @Test
    void constructor_shouldThrowWhenAmountIsNull() {
        assertThatThrownBy(() -> new Price(null, "EUR"))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("Price amount must not be null");
    }

    @Test
    void constructor_shouldThrowWhenAmountIsZero() {
        assertThatThrownBy(() -> new Price(BigDecimal.ZERO, "EUR"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Price amount must be greater than 0");
    }

    @Test
    void constructor_shouldThrowWhenAmountIsNegative() {
        assertThatThrownBy(() -> new Price(new BigDecimal("-1.00"), "EUR"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Price amount must be greater than 0");
    }

    // --- currency validation ---

    @Test
    void constructor_shouldThrowWhenCurrencyIsNull() {
        assertThatThrownBy(() -> new Price(new BigDecimal("10.00"), null))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("Price currency must not be null");
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "   ", "\t"})
    void constructor_shouldThrowWhenCurrencyIsBlank(String blankcurrency) {
        assertThatThrownBy(() -> new Price(new BigDecimal("10.00"), blankcurrency))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Price currency must not be blank");
    }

    // --- equality ---

    @Test
    void equality_shouldBeBasedOnAmountAndCurrency() {
        Price a = new Price(new BigDecimal("5.00"), "USD");
        Price b = new Price(new BigDecimal("5.00"), "USD");
        assertThat(a).isEqualTo(b);
    }
}

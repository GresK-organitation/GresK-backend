package com.gresk.modules.event.domain.model;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static com.gresk.modules.event.domain.model.EventStatus.*;
import static org.assertj.core.api.Assertions.assertThat;

class EventStatusTest {

    static Stream<Arguments> validTransitions() {
        return Stream.of(
                Arguments.of(DRAFT, PUBLISHED),
                Arguments.of(DRAFT, CANCELLED),
                Arguments.of(PUBLISHED, FINISHED),
                Arguments.of(PUBLISHED, CANCELLED)
        );
    }

    static Stream<Arguments> invalidTransitions() {
        return Stream.of(
                Arguments.of(DRAFT, FINISHED),
                Arguments.of(DRAFT, DRAFT),
                Arguments.of(PUBLISHED, DRAFT),
                Arguments.of(PUBLISHED, PUBLISHED),
                Arguments.of(FINISHED, DRAFT),
                Arguments.of(FINISHED, PUBLISHED),
                Arguments.of(FINISHED, FINISHED),
                Arguments.of(FINISHED, CANCELLED),
                Arguments.of(CANCELLED, DRAFT),
                Arguments.of(CANCELLED, PUBLISHED),
                Arguments.of(CANCELLED, FINISHED),
                Arguments.of(CANCELLED, CANCELLED)
        );
    }

    @ParameterizedTest(name = "{0} → {1} should be allowed")
    @MethodSource("validTransitions")
    void canTransitionTo_shouldReturnTrueForAllowedTransitions(EventStatus from, EventStatus to) {
        assertThat(from.canTransitionTo(to)).isTrue();
    }

    @ParameterizedTest(name = "{0} → {1} should be forbidden")
    @MethodSource("invalidTransitions")
    void canTransitionTo_shouldReturnFalseForForbiddenTransitions(EventStatus from, EventStatus to) {
        assertThat(from.canTransitionTo(to)).isFalse();
    }
}

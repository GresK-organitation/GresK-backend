package com.gresk.modules.tendencias.chronicle.domain.model;

import com.gresk.modules.tendencias.chronicle.domain.exception.InvalidChronicleTransitionException;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ChronicleTest {

    private Chronicle aPublishedChronicle() {
        return Chronicle.publish(
                "Título de prueba",
                ChronicleExcerpt.truncate("Un resumen corto."),
                "https://medio.example/articulo",
                "guid-1",
                new SourceAttribution("Medio de prueba", "https://medio.example"),
                FeedSourceId.generate(),
                Instant.now());
    }

    @Test
    void publishCreaLaCronicaYaEnPublished() {
        Chronicle chronicle = aPublishedChronicle();
        assertThat(chronicle.getStatus()).isEqualTo(ChronicleStatus.PUBLISHED);
    }

    @Test
    void hideDesdePublishedFunciona() {
        Chronicle chronicle = aPublishedChronicle();
        chronicle.hide();
        assertThat(chronicle.getStatus()).isEqualTo(ChronicleStatus.HIDDEN);
    }

    @Test
    void hideDosVecesLanzaExcepcion() {
        Chronicle chronicle = aPublishedChronicle();
        chronicle.hide();
        assertThatThrownBy(chronicle::hide).isInstanceOf(InvalidChronicleTransitionException.class);
    }
}

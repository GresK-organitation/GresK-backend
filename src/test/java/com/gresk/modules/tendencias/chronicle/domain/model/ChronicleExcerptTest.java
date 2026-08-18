package com.gresk.modules.tendencias.chronicle.domain.model;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ChronicleExcerptTest {

    @Test
    void elConstructorRechazaTextoMasLargoQueElMaximo() {
        String demasiadoLargo = "a".repeat(ChronicleExcerpt.MAX_LENGTH + 1);
        assertThatThrownBy(() -> new ChronicleExcerpt(demasiadoLargo))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void elConstructorRechazaTextoEnBlanco() {
        assertThatThrownBy(() -> new ChronicleExcerpt("   "))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void truncateNuncaProduceUnResultadoFueraDeLimite() {
        String textoLargo = "palabra ".repeat(100);
        ChronicleExcerpt excerpt = ChronicleExcerpt.truncate(textoLargo);
        assertThat(excerpt.value().length()).isLessThanOrEqualTo(ChronicleExcerpt.MAX_LENGTH);
    }

    @Test
    void truncateDejaIntactoUnTextoCorto() {
        ChronicleExcerpt excerpt = ChronicleExcerpt.truncate("  Un resumen corto.  ");
        assertThat(excerpt.value()).isEqualTo("Un resumen corto.");
    }
}

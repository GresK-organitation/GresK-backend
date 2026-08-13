package com.gresk.modules.journal.domain.model;

import com.gresk.modules.journal.domain.exception.InvalidRatingCriterionException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class RatingCriterionTest {

    @Test
    void creaUnCriterioValidoYRecortaEspacios() {
        RatingCriterion criterion = RatingCriterion.of("  luces  ", 4);

        assertEquals("luces", criterion.label());
        assertEquals(4, criterion.value());
    }

    @Test
    void rechazaEtiquetaVacia() {
        assertThrows(InvalidRatingCriterionException.class, () -> RatingCriterion.of("  ", 3));
    }

    @Test
    void rechazaEtiquetaDemasiadoLarga() {
        String label = "a".repeat(61);

        assertThrows(InvalidRatingCriterionException.class, () -> RatingCriterion.of(label, 3));
    }

    @Test
    void rechazaValorFueraDeRango() {
        assertThrows(InvalidRatingCriterionException.class, () -> RatingCriterion.of("luces", 0));
        assertThrows(InvalidRatingCriterionException.class, () -> RatingCriterion.of("luces", 6));
    }
}

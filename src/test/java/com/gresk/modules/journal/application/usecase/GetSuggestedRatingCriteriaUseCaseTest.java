package com.gresk.modules.journal.application.usecase;

import com.gresk.shared.domain.MusicGenre;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class GetSuggestedRatingCriteriaUseCaseTest {

    private final GetSuggestedRatingCriteriaUseCase useCase = new GetSuggestedRatingCriteriaUseCase();

    @Test
    void devuelveSugerenciasParaCadaGeneroDelCatalogo() {
        for (MusicGenre genre : MusicGenre.values()) {
            List<String> suggestions = useCase.execute(genre);
            assertNotNull(suggestions);
            assertFalse(suggestions.isEmpty());
        }
    }

    @Test
    void generoNuloDevuelveCriteriosPorDefecto() {
        List<String> suggestions = useCase.execute(null);

        assertFalse(suggestions.isEmpty());
    }
}

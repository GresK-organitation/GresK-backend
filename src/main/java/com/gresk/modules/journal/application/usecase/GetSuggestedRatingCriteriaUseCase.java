package com.gresk.modules.journal.application.usecase;

import com.gresk.shared.domain.MusicGenre;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * Static, per-genre suggested rating criteria used only to prefill the
 * client's "add criteria" form — never persisted (a template is not
 * saved/reused across entries, per product decision).
 */
@Service
public class GetSuggestedRatingCriteriaUseCase {

    private static final List<String> DEFAULT_CRITERIA =
            List.of("Sonido", "Ambiente", "Artista", "Recinto");

    private static final Map<MusicGenre, List<String>> SUGGESTIONS_BY_GENRE = Map.ofEntries(
            Map.entry(MusicGenre.JAZZ,       List.of("Improvisación", "Sonido", "Interacción con el público", "Ambiente")),
            Map.entry(MusicGenre.LATIN_JAZZ, List.of("Improvisación", "Sonido", "Interacción con el público", "Ambiente")),
            Map.entry(MusicGenre.TECHNO,     List.of("Luces", "Selección musical", "Sonido", "Energía de la pista")),
            Map.entry(MusicGenre.HOUSE,      List.of("Luces", "Selección musical", "Sonido", "Energía de la pista")),
            Map.entry(MusicGenre.ELECTRONIC, List.of("Luces", "Selección musical", "Sonido", "Energía de la pista")),
            Map.entry(MusicGenre.CLASSICAL,  List.of("Acústica de la sala", "Interpretación", "Ambiente")),
            Map.entry(MusicGenre.ROCK,       List.of("Energía en directo", "Sonido", "Setlist", "Ambiente")),
            Map.entry(MusicGenre.METAL,      List.of("Energía en directo", "Sonido", "Setlist", "Ambiente")),
            Map.entry(MusicGenre.PUNK,       List.of("Energía en directo", "Sonido", "Setlist", "Ambiente")),
            Map.entry(MusicGenre.FLAMENCO,   List.of("Duende", "Interpretación", "Ambiente")),
            Map.entry(MusicGenre.HIP_HOP,    List.of("Flow", "Sonido", "Interacción con el público", "Ambiente")),
            Map.entry(MusicGenre.TRAP,       List.of("Flow", "Sonido", "Interacción con el público", "Ambiente")),
            Map.entry(MusicGenre.R_AND_B,    List.of("Voz en directo", "Sonido", "Ambiente")),
            Map.entry(MusicGenre.REGGAETON,  List.of("Energía de la pista", "Sonido", "Interacción con el público")),
            Map.entry(MusicGenre.POP,        List.of("Puesta en escena", "Sonido", "Setlist", "Ambiente")),
            Map.entry(MusicGenre.INDIE,      List.of("Sonido", "Setlist", "Ambiente")),
            Map.entry(MusicGenre.SURPRISE,   DEFAULT_CRITERIA)
    );

    public List<String> execute(MusicGenre genre) {
        if (genre == null) return DEFAULT_CRITERIA;
        return SUGGESTIONS_BY_GENRE.getOrDefault(genre, DEFAULT_CRITERIA);
    }
}

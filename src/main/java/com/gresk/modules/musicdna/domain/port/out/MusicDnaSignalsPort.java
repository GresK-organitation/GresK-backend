package com.gresk.modules.musicdna.domain.port.out;

import com.gresk.modules.user.domain.model.UserId;

import java.time.Instant;
import java.util.List;

public interface MusicDnaSignalsPort {
    MusicDnaSignals findSignals(UserId userId);
    List<UserId> findUserIdsWithActivitySince(Instant since);

    /**
     * Top-N géneros del usuario por frecuencia (reviews + journal entries),
     * usado por Discovery para el botón "Sorpréndeme" (excluir géneros
     * habituales). Devuelve los nombres de {@code MusicGenre} ordenados de
     * más a menos frecuente.
     */
    List<String> findTopGenres(UserId userId, int limit);
}

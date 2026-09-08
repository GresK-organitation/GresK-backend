package com.gresk.modules.show.domain.port.out;

import com.gresk.modules.show.domain.model.ShowId;
import com.gresk.modules.show.domain.model.ShowLogEntry;

import java.util.List;

/** Repositorio append-only de la bitácora, independiente del agregado {@code Show} (ver Javadoc de {@code Show}). */
public interface ShowLogRepositoryPort {
    ShowLogEntry save(ShowLogEntry entry);
    List<ShowLogEntry> findByShowId(ShowId showId);
}

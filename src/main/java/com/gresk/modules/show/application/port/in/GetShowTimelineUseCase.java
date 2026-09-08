package com.gresk.modules.show.application.port.in;

import com.gresk.modules.show.domain.model.ShowLogEntry;

import java.util.List;

/** Devuelve la bitácora completa (comunicaciones, decisiones, incidencias y cambios de estado automáticos). */
public interface GetShowTimelineUseCase {
    List<ShowLogEntry> execute(String showId, String promoterId);
}

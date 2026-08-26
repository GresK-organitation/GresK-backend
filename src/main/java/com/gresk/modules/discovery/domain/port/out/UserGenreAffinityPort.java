package com.gresk.modules.discovery.domain.port.out;

import com.gresk.modules.user.domain.model.UserId;

/** Lookup de solo lectura al módulo `musicdna` para el botón "Sorpréndeme". */
public interface UserGenreAffinityPort {
    UserGenreAffinity findTopGenres(UserId userId, int limit);
}

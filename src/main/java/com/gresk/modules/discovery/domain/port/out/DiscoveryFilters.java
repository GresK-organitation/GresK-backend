package com.gresk.modules.discovery.domain.port.out;

import com.gresk.modules.discovery.domain.model.SizeTier;
import com.gresk.shared.domain.MusicGenre;

import java.util.Optional;
import java.util.Set;

/**
 * Filtros de búsqueda de Discovery. Los filtros geográficos ("mi ciudad",
 * "mi país") son de coincidencia de texto — no hay lat/long ni distancia
 * real, consistente con {@code users.city} (texto libre).
 */
public record DiscoveryFilters(
        Set<SizeTier> sizeTiers,
        Optional<String> city,
        Optional<String> country,
        Set<MusicGenre> genres,
        Set<MusicGenre> excludeGenres,
        Optional<LiveFilter> liveFilter,
        Optional<String> userCityForLiveFilter,
        boolean onlyWithReviews,
        boolean onlyWithDemandInMyCity,
        Optional<String> userCityForDemandFilter,
        boolean onlyNewOnPlatform,
        boolean onlyWithMusicBrainz,
        DiscoverySortOption sort,
        int page,
        int size
) {
    public static DiscoveryFilters defaults() {
        return new DiscoveryFilters(
                Set.of(), Optional.empty(), Optional.empty(), Set.of(), Set.of(),
                Optional.empty(), Optional.empty(), false, false, Optional.empty(),
                false, false, DiscoverySortOption.GRESK_SCORE, 0, 20
        );
    }
}

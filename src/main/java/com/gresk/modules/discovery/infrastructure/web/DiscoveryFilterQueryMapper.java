package com.gresk.modules.discovery.infrastructure.web;

import com.gresk.modules.discovery.domain.model.SizeTier;
import com.gresk.modules.discovery.domain.port.out.DiscoveryFilters;
import com.gresk.modules.discovery.domain.port.out.DiscoverySortOption;
import com.gresk.modules.discovery.domain.port.out.LiveFilter;
import com.gresk.shared.domain.MusicGenre;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/** Traduce los query params del endpoint de búsqueda a {@link DiscoveryFilters}. */
@Component
public class DiscoveryFilterQueryMapper {

    public DiscoveryFilters toFilters(
            Set<String> sizeTiers, String city, String country, Set<String> genres, Set<String> excludeGenres,
            String liveFilter, String myCity, boolean onlyWithReviews, boolean onlyWithDemandInMyCity,
            boolean onlyNewOnPlatform, boolean onlyWithMusicBrainz, String sort, int page, int size) {

        return new DiscoveryFilters(
                toSizeTiers(sizeTiers),
                Optional.ofNullable(city),
                Optional.ofNullable(country),
                toGenres(genres),
                toGenres(excludeGenres),
                Optional.ofNullable(liveFilter).map(LiveFilter::valueOf),
                Optional.ofNullable(myCity),
                onlyWithReviews,
                onlyWithDemandInMyCity,
                Optional.ofNullable(myCity),
                onlyNewOnPlatform,
                onlyWithMusicBrainz,
                sort != null ? DiscoverySortOption.valueOf(sort) : DiscoverySortOption.GRESK_SCORE,
                page, size
        );
    }

    private Set<SizeTier> toSizeTiers(Set<String> values) {
        if (values == null) return Set.of();
        return values.stream().map(SizeTier::valueOf).collect(Collectors.toSet());
    }

    private Set<MusicGenre> toGenres(Set<String> values) {
        if (values == null) return Set.of();
        return values.stream().map(MusicGenre::valueOf).collect(Collectors.toSet());
    }
}

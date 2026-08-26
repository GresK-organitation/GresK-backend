package com.gresk.modules.discovery.infrastructure.adapter;

import com.gresk.modules.discovery.domain.model.SizeTier;
import com.gresk.modules.discovery.domain.port.out.DiscoveryArtistSummary;
import com.gresk.modules.discovery.domain.port.out.DiscoveryFilters;
import com.gresk.modules.discovery.domain.port.out.SearchArtistsPort;
import com.gresk.modules.discovery.infrastructure.persistence.DiscoveryArtistQueryRepository;
import com.gresk.modules.discovery.infrastructure.persistence.DiscoveryArtistRow;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class JpaSearchArtistsAdapter implements SearchArtistsPort {

    private final DiscoveryArtistQueryRepository queryRepository;

    @Override
    public List<DiscoveryArtistSummary> search(DiscoveryFilters filters) {
        int limit = filters.size();
        long offset = (long) filters.page() * filters.size();

        List<DiscoveryArtistRow> rows = switch (filters.sort()) {
            case SMALLEST -> queryRepository.searchBySmallest(
                    sizeTiersCsv(filters), city(filters), country(filters), genresCsv(filters.genres()),
                    genresCsv(filters.excludeGenres()), filters.onlyWithReviews(), filters.onlyWithDemandInMyCity(),
                    demandCity(filters), filters.onlyNewOnPlatform(), filters.onlyWithMusicBrainz(),
                    liveFilter(filters), liveCity(filters), limit, offset);
            case MOST_RECENT -> queryRepository.searchByMostRecent(
                    sizeTiersCsv(filters), city(filters), country(filters), genresCsv(filters.genres()),
                    genresCsv(filters.excludeGenres()), filters.onlyWithReviews(), filters.onlyWithDemandInMyCity(),
                    demandCity(filters), filters.onlyNewOnPlatform(), filters.onlyWithMusicBrainz(),
                    liveFilter(filters), liveCity(filters), limit, offset);
            case EARLIEST_DISCOVERY -> queryRepository.searchByEarliestDiscovery(
                    sizeTiersCsv(filters), city(filters), country(filters), genresCsv(filters.genres()),
                    genresCsv(filters.excludeGenres()), filters.onlyWithReviews(), filters.onlyWithDemandInMyCity(),
                    demandCity(filters), filters.onlyNewOnPlatform(), filters.onlyWithMusicBrainz(),
                    liveFilter(filters), liveCity(filters), limit, offset);
            case GRESK_SCORE -> queryRepository.searchByGreskScore(
                    sizeTiersCsv(filters), city(filters), country(filters), genresCsv(filters.genres()),
                    genresCsv(filters.excludeGenres()), filters.onlyWithReviews(), filters.onlyWithDemandInMyCity(),
                    demandCity(filters), filters.onlyNewOnPlatform(), filters.onlyWithMusicBrainz(),
                    liveFilter(filters), liveCity(filters), limit, offset);
        };

        return rows.stream().map(this::toSummary).toList();
    }

    @Override
    public long count(DiscoveryFilters filters) {
        return queryRepository.count(
                sizeTiersCsv(filters), city(filters), country(filters), genresCsv(filters.genres()),
                genresCsv(filters.excludeGenres()), filters.onlyWithReviews(), filters.onlyWithDemandInMyCity(),
                demandCity(filters), filters.onlyNewOnPlatform(), filters.onlyWithMusicBrainz(),
                liveFilter(filters), liveCity(filters));
    }

    private DiscoveryArtistSummary toSummary(DiscoveryArtistRow row) {
        List<String> genres = row.getGenres() == null || row.getGenres().isBlank()
                ? List.of() : Arrays.asList(row.getGenres().split(","));
        return new DiscoveryArtistSummary(
                row.getArtistId(), row.getName(), row.getOrigin(), genres, row.getImageAssetId(),
                SizeTier.valueOf(row.getSizeTier()), row.getSpotifyPopularity(), row.getGreskScore(),
                row.getAvgRating() != null ? row.getAvgRating() : 0.0,
                row.getGreskReviewCount() != null ? row.getGreskReviewCount() : 0,
                row.getGreskDemandCount() != null ? row.getGreskDemandCount() : 0,
                Boolean.TRUE.equals(row.getHasUpcomingEvents()), row.getNextEventDate(), row.getNextEventCity()
        );
    }

    private String sizeTiersCsv(DiscoveryFilters filters) {
        return csv(filters.sizeTiers().stream().map(SizeTier::name).collect(Collectors.toSet()));
    }

    private String genresCsv(Set<com.gresk.shared.domain.MusicGenre> genres) {
        return csv(genres.stream().map(Enum::name).collect(Collectors.toSet()));
    }

    private String csv(Set<String> values) {
        return values.isEmpty() ? "" : String.join(",", values);
    }

    private String city(DiscoveryFilters filters) {
        return filters.city().orElse(null);
    }

    private String country(DiscoveryFilters filters) {
        return filters.country().orElse(null);
    }

    private String demandCity(DiscoveryFilters filters) {
        return filters.userCityForDemandFilter().orElse(null);
    }

    private String liveFilter(DiscoveryFilters filters) {
        return filters.liveFilter().map(Enum::name).orElse(null);
    }

    private String liveCity(DiscoveryFilters filters) {
        return filters.userCityForLiveFilter().orElse(null);
    }
}

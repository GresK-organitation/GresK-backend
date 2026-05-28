package com.gresk.modules.user.infrastructure.adapters.gresk;

import com.gresk.modules.artist.infrastructure.persistence.ArtistEntity;
import com.gresk.modules.artist.infrastructure.persistence.ArtistJpaRepository;
import com.gresk.modules.artist.infrastructure.persistence.ArtistMetricsSnapshotEntity;
import com.gresk.modules.artist.infrastructure.persistence.ArtistMetricsSnapshotJpaRepository;
import com.gresk.modules.event.domain.model.EventStatus;
import com.gresk.modules.user.domain.port.out.GresKArtistSpotifyPort;
import com.gresk.modules.user.infrastructure.persistence.UserEventQueryRepository;
import com.gresk.shared.domain.MusicGenre;
import com.gresk.shared.domain.recommendation.RecommendationLabel;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class GresKArtistSpotifyAdapter implements GresKArtistSpotifyPort {

    private final ArtistJpaRepository                  artistRepo;
    private final ArtistMetricsSnapshotJpaRepository   metricsRepo;
    private final UserEventQueryRepository             eventQueryRepository;

    @Override
    @Cacheable(value = "gresKArtistsByGenre", key = "#genres.toString() + #city")
    public List<GresKArtistSpotifyData> findByGenres(Set<MusicGenre> genres, String city) {
        return artistRepo.findByGenresAndHasSpotifyId(genres)
                .stream()
                .map(artist -> {
                    Optional<ArtistMetricsSnapshotEntity> latest =
                            metricsRepo.findFirstByArtistIdOrderBySnapshotDateDesc(artist.getId());
                    Optional<ArtistMetricsSnapshotEntity> previous = latest.isPresent()
                            ? metricsRepo.findFirstByArtistIdAndSnapshotDateBeforeOrderBySnapshotDateDesc(
                                    artist.getId(), latest.get().getSnapshotDate())
                            : Optional.empty();

                    RecommendationLabel label = calculateLabel(artist, latest, previous, city);

                    return new GresKArtistSpotifyData(
                            artist.getId(),
                            artist.getSpotifyArtistId(),
                            artist.getName(),
                            artist.getGenres(),
                            label
                    );
                })
                .toList();
    }

    private RecommendationLabel calculateLabel(
            ArtistEntity artist,
            Optional<ArtistMetricsSnapshotEntity> latest,
            Optional<ArtistMetricsSnapshotEntity> previous,
            String city) {

        boolean hasConcert = eventQueryRepository.existsUpcomingConcertForArtist(
                artist.getId(),
                city,
                Instant.now().plus(1, ChronoUnit.DAYS),
                Instant.now().plus(7, ChronoUnit.DAYS),
                EventStatus.PUBLISHED
        );
        if (hasConcert) return RecommendationLabel.PLAYING_THIS_WEEK;

        if (latest.isPresent() && latest.get().getLastReleaseDate() != null
                && ChronoUnit.DAYS.between(latest.get().getLastReleaseDate(), LocalDate.now()) <= 7) {
            return RecommendationLabel.NEW_RELEASE;
        }

        if (latest.isPresent() && previous.isPresent()
                && latest.get().getSpotifyPopularity() != null
                && previous.get().getSpotifyPopularity() != null) {
            int delta = latest.get().getSpotifyPopularity() - previous.get().getSpotifyPopularity();
            if (delta >= 5) return RecommendationLabel.TRENDING_THIS_WEEK;
        }

        return RecommendationLabel.NUEVO_ARTISTA;
    }
}

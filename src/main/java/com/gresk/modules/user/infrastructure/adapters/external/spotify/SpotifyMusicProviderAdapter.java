package com.gresk.modules.user.infrastructure.adapters.external.spotify;

import com.gresk.modules.user.domain.model.MusicRecommendation;
import com.gresk.modules.user.domain.port.out.GresKArtistSpotifyPort;
import com.gresk.modules.user.domain.port.out.MusicRecommendationProvider;
import com.gresk.modules.user.domain.port.out.ShownArtistsPort;
import com.gresk.shared.domain.MusicGenre;
import com.gresk.shared.infrastructure.spotify.SpotifyApiClient;
import com.gresk.shared.infrastructure.spotify.SpotifyDto;
import com.gresk.shared.infrastructure.spotify.SpotifyTokenService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class SpotifyMusicProviderAdapter implements MusicRecommendationProvider {

    private final SpotifyApiClient         apiClient;
    private final SpotifyTokenService      tokenService;
    private final GresKArtistSpotifyPort   gresKPort;
    private final ShownArtistsPort         shownArtistsPort;

    @Override
    public Set<MusicRecommendation> getSpotifyTopTracks(
            Set<MusicGenre> genres, String city, UUID userId) {
        try {
            String token = tokenService.getBearerToken();
            Set<String> shown = shownArtistsPort.getShownIds(userId);

            List<GresKArtistSpotifyPort.GresKArtistSpotifyData> candidates =
                    gresKPort.findByGenres(genres, city)
                            .stream()
                            .filter(a -> !shown.contains(a.spotifyArtistId()))
                            .toList();

            Set<MusicRecommendation> recommendations = new HashSet<>();
            Set<String> newlyShown = new HashSet<>();

            for (var candidate : candidates) {
                if (recommendations.size() >= 10) break;
                SpotifyDto.SpotifyTrackDTO track = fetchTopTrackForArtist(token, candidate.spotifyArtistId());
                if (track != null) {
                    recommendations.add(
                            mapToDomain(track, genres)
                                    .withLabel(candidate.suggestedLabel())
                                    .withIsGresKArtist(true)
                    );
                    newlyShown.add(candidate.spotifyArtistId());
                }
            }

            if (recommendations.size() < 10) {
                for (MusicGenre genre : genres) {
                    if (recommendations.size() >= 10) break;
                    recommendations.addAll(findTracksByGenre(token, genre, city));
                }
            }

            if (!newlyShown.isEmpty()) {
                shownArtistsPort.markShown(userId, newlyShown);
            }

            return recommendations;
        } catch (Exception e) {
            log.error("Recommendation failed: {}", e.getMessage());
            return Set.of();
        }
    }

    private SpotifyDto.SpotifyTrackDTO fetchTopTrackForArtist(String token, String spotifyArtistId) {
        try {
            var response = apiClient.searchTracks(
                    token, "artist:" + spotifyArtistId, "track", "ES", 5);
            if (isResponseEmpty(response)) return null;
            return response.tracks().items().get(0);
        } catch (Exception e) {
            log.warn("Failed to fetch track for GresK artist {}: {}", spotifyArtistId, e.getMessage());
            return null;
        }
    }

    private Set<MusicRecommendation> findTracksByGenre(String token, MusicGenre genre, String city) {
        int year = LocalDate.now().getYear();
        String genreKey = genre.getSpotifyKey();

        String query1 = String.format("%s genre:\"%s\" year:%d-%d", city.trim(), genreKey, year - 1, year);
        var response = executeSearch(token, query1);

        if (isResponseEmpty(response)) {
            log.info("level 2: search just for year and genre {}", genreKey);
            String query2 = String.format("genre:\"%s\" year:%d-%d", genreKey, year - 1, year);
            response = executeSearch(token, query2);
        }

        if (isResponseEmpty(response)) {
            log.info("level 3: expand years for same genre {}", genreKey);
            String query3 = String.format("genre:\"%s\" year:%d-%d", genreKey, year - 4, year);
            response = executeSearch(token, query3);
        }

        if (isResponseEmpty(response)) return Set.of();

        return response.tracks().items().stream()
                .filter(t -> t.popularity() < 50)
                .limit(3)
                .map(t -> mapToDomain(t, Set.of(genre)))
                .collect(Collectors.toSet());
    }

    private SpotifyDto.SpotifySearchResponse executeSearch(String token, String query) {
        try {
            return apiClient.searchTracks(token, query, "track", "ES", 10);
        } catch (Exception e) {
            log.warn("Query fallida [{}]: {}", query, e.getMessage());
            return null;
        }
    }

    private boolean isResponseEmpty(SpotifyDto.SpotifySearchResponse response) {
        return response == null || response.tracks() == null
                || response.tracks().items() == null || response.tracks().items().isEmpty();
    }

    private MusicRecommendation mapToDomain(SpotifyDto.SpotifyTrackDTO track, Set<MusicGenre> originalGenres) {
        return new MusicRecommendation(
                track.name(),
                (track.artists() == null || track.artists().isEmpty())
                        ? "Unknown Artist" : track.artists().get(0).name(),
                (track.externalUrls() != null)
                        ? track.externalUrls().getOrDefault("spotify", "#") : "#",
                (track.album() != null && track.album().images() != null && !track.album().images().isEmpty())
                        ? track.album().images().get(0).url() : null,
                originalGenres.stream().findFirst().orElse(MusicGenre.SURPRISE)
        );
    }
}

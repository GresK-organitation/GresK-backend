package com.gresk.modules.user.infrastructure.adapters.external.spotify;

import com.gresk.modules.user.domain.model.MusicRecommendation;
import com.gresk.modules.user.domain.port.out.MusicRecommendationProvider;
import com.gresk.modules.user.infrastructure.config.SpotifyConfig;
import com.gresk.shared.domain.MusicGenre;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.cache.annotation.Cacheable;

import java.time.LocalDate;
import java.util.Base64;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class SpotifyMusicProviderAdapter implements MusicRecommendationProvider {
    private final SpotifyAuthClient authClient;
    private final SpotifyApiClient apiClient;
    private final SpotifyConfig config;

    @Override
    @Cacheable(value = "spotifyRecommendations", key = "#genres.toString() + #city", unless = "#result.isEmpty()")
    public Set<MusicRecommendation> getSpotifyTopTracks(Set<MusicGenre> genres, String city) {
        try {
            String authHeader = "Basic " + Base64.getEncoder()
                    .encodeToString((config.getClientId() + ":" + config.getClientSecret()).getBytes());

            Map<String, String> params = Map.of("grant_type", "client_credentials");
            var tokenResponse = authClient.getToken(authHeader, params);
            String token = "Bearer " + tokenResponse.accessToken();

            String genresQuery = genres.stream()
                    .map(g -> "genre:\"" + g.getSpotifyKey() + "\"")
                    .collect(Collectors.joining(" OR "));

            var response = getBestResults(token, city, genresQuery);

            if (isResponseEmpty(response)) {
                return Set.of();
            }

            return response.tracks().items().stream()
                    //.filter(t -> t.popularity() > 50)
                    .map(t -> mapToDomain(t, genres))
                    .collect(Collectors.toSet());

        } catch (Exception e) {
            log.error("Spotify discovery failed — genres={} city={} error={}", genres, city, e.getMessage());
            return Set.of();
        }
    }

    private SpotifyDto.SpotifySearchResponse getBestResults(String token, String city, String genresQuery) {
        int year = LocalDate.now().getYear();
        String cleanCity = (city != null) ? city.trim().toLowerCase() : "";
        String country = "ES";
        int limit = 5;

        String query1 = String.format("\"%s\" (%s) year:%d-%d", cleanCity, genresQuery, year - 1, year);
        SpotifyDto.SpotifySearchResponse response = executeSearch(token, query1, country, limit);

//        if (isResponseEmpty(response)) {
//            log.info("Nivel 2: Sin resultados en {}, ampliando rango...", cleanCity);
//            String query2 = String.format("(%s) year:%d-%d", genresQuery, year - 4, year);
//            response = executeSearch(token, query2, country, limit);
//        }

        if (isResponseEmpty(response)) {
            log.info("Nivel 3: Buscando solo por género...");
            response = executeSearch(token, genresQuery, country, limit);
        }

        return response;
    }

    private SpotifyDto.SpotifySearchResponse executeSearch(String token, String query, String market, int limit) {
        log.debug("Ejecutando búsqueda: q=[{}]", query);
        try {
            return apiClient.searchEmergingArtists(token, query, "track", market, limit);
        } catch (Exception e) {
            log.warn("Query fallida [{}]: {}", query, e.getMessage());
            return null;
        }
    }

    private boolean isResponseEmpty(SpotifyDto.SpotifySearchResponse response) {
        return response == null ||
                response.tracks() == null ||
                response.tracks().items() == null ||
                response.tracks().items().isEmpty();
    }

    private MusicRecommendation mapToDomain(SpotifyDto.SpotifyTrackDTO track, Set<MusicGenre> originalGenres) {
        return new MusicRecommendation(
                track.name(),
                (track.artists() == null || track.artists().isEmpty()) ? "Unknown Artist" : track.artists().get(0).name(),
                (track.externalUrls() != null) ? track.externalUrls().getOrDefault("spotify", "#") : "#",
                (track.album() != null && track.album().images() != null && !track.album().images().isEmpty())
                        ? track.album().images().get(0).url() : null,
                originalGenres.stream().findFirst().orElse(MusicGenre.SURPRISE)
        );
    }
}
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
            // 1. Obtención de Token
            String authHeader = "Basic " + Base64.getEncoder()
                    .encodeToString((config.getClientId() + ":" + config.getClientSecret()).getBytes());
            var tokenResponse = authClient.getToken(authHeader, Map.of("grant_type", "client_credentials"));
            String token = "Bearer " + tokenResponse.accessToken();

            // 2. Procesamos cada género de forma independiente para garantizar equidad
            return genres.stream()
                    .flatMap(genre -> findTracksByGenre(token, genre, city).stream())
                    .collect(Collectors.toSet());

        } catch (Exception e) {
            log.error("Spotify discovery failed: {}", e.getMessage());
            return Set.of();
        }
    }

    private Set<MusicRecommendation> findTracksByGenre(String token, MusicGenre genre, String city) {
        int year = LocalDate.now().getYear();
        String genreKey = genre.getSpotifyKey();

        // PRIORIDAD 1: Ciudad + Género + Años Recientes (2025-2026)
        // Usamos la ciudad sin comillas estrictas para dar más margen al buscador
        String query1 = String.format("%s genre:\"%s\" year:%d-%d", city.trim(), genreKey, year - 1, year);
        var response = executeSearch(token, query1);

        // PRIORIDAD 2: Solo Género + Años Recientes (Si la ciudad falla)
        if (isResponseEmpty(response)) {
            log.info("level 2: search just for year and genre{}", genreKey);
            String query2 = String.format("genre:\"%s\" year:%d-%d", genreKey, year - 1, year);
            response = executeSearch(token, query2);
        }

        // PRIORIDAD 3: Solo Género + Ampliar Años (2022-2026)
        if (isResponseEmpty(response)) {
            log.info("level 3: expand years for same genre{}", genreKey);
            String query3 = String.format("genre:\"%s\" year:%d-%d", genreKey, year - 4, year);
            response = executeSearch(token, query3);
        }

        if (isResponseEmpty(response)) return Set.of();

        // Mapeamos y limitamos a 3 por género para que sea equitativo
        return response.tracks().items().stream()
                .filter(t -> t.popularity() < 50) // Mantenemos el espíritu emergente
                .limit(3)
                .map(t -> mapToDomain(t, Set.of(genre)))
                .collect(Collectors.toSet());
    }

    private SpotifyDto.SpotifySearchResponse executeSearch(String token, String query) {
        try {
            return apiClient.searchEmergingArtists(token, query, "track", "ES", 10);
        } catch (Exception e) {
            log.warn("Query fallida [{}]: {}", query, e.getMessage());
            return null;
        }
    }

    private boolean isResponseEmpty(SpotifyDto.SpotifySearchResponse response) {
        return response == null || response.tracks() == null ||
                response.tracks().items() == null || response.tracks().items().isEmpty();
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
package com.gresk.modules.user.infrastructure.adapters.external.spotify;

import com.gresk.modules.user.domain.model.MusicRecommendation;
import com.gresk.modules.user.domain.port.out.MusicRecommendationProvider;
import com.gresk.modules.user.infrastructure.config.SpotifyConfig;
import com.gresk.shared.domain.MusicGenre;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.cache.annotation.Cacheable;

import java.util.Base64;
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
    @Cacheable(value = "spotifyRecommendations", key = "#genres", unless = "#result.isEmpty()")
    public Set<MusicRecommendation> getSpotifyTopTracks(Set<MusicGenre> genres) {
        try {
            // 1. Handshake de Seguridad
            String authHeader = "Basic " + Base64.getEncoder()
                    .encodeToString((config.getClientId() + ":" + config.getClientSecret()).getBytes());

            // CORRECCIÓN: Spotify espera un POST. Al pasar los parámetros como Form,
            // Feign calculará automáticamente el Content-Length evitando el error 411.
            var tokenResponse = authClient.getToken(authHeader, "client_credentials");
            String token = "Bearer " + tokenResponse.accessToken();

            // 2. Selección de Semillas (Spotify solo admite 5)
            String seedGenres = genres.stream()
                    .limit(5)
                    .map(MusicGenre::getSpotifyKey)
                    .collect(Collectors.joining(","));

            if (seedGenres.isEmpty()) seedGenres = MusicGenre.SURPRISE.getSpotifyKey();

            // 3. Petición a la API
            var response = apiClient.getRecommendations(token, seedGenres, 6);

            // 4. Transformación a Dominio
            if (response == null || response.tracks() == null) return Set.of();

            return response.tracks().stream()
                    .map(track -> mapToDomain(track, genres))
                    .collect(Collectors.toSet());

        } catch (Exception e) {
            log.error("Spotify recommendations failed — genres={} error={}", genres, e.getMessage());
            return Set.of();
        }
    }

    private MusicRecommendation mapToDomain(SpotifyDto.SpotifyTrackDTO track, Set<MusicGenre> originalGenres) {
        return new MusicRecommendation(
                track.name(),
                (track.artists() == null || track.artists().isEmpty()) ? "Unknown" : track.artists().get(0).name(),
                (track.externalUrls() != null) ? track.externalUrls().getOrDefault("spotify", "#") : "#",
                (track.album() != null && track.album().images() != null && !track.album().images().isEmpty())
                        ? track.album().images().get(0).url() : null,
                originalGenres.stream().findFirst().orElse(MusicGenre.SURPRISE)
        );
    }
}
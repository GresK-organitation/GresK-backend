package com.gresk.modules.user.infrastructure.adapters.external.spotify;

import com.gresk.modules.user.domain.model.MusicRecommendation;
import com.gresk.modules.user.domain.port.out.GresKArtistSpotifyPort;
import com.gresk.modules.user.domain.port.out.MusicRecommendationProvider;
import com.gresk.modules.user.domain.port.out.ShownArtistsPort;
import com.gresk.shared.domain.MusicGenre;
import com.gresk.shared.infrastructure.spotify.SpotifyApiClient;
import com.gresk.shared.infrastructure.spotify.SpotifyDto;
import com.gresk.shared.domain.recommendation.RecommendationLabel;
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

// ── Fallback search categories ────────────────────────────────────────────────
// Orientadas a música local/española, underground y artistas emergentes,
// alineadas con el espíritu de GresK (escenas locales, sala pequeña, pocos plays).

@Slf4j
@Component
@RequiredArgsConstructor
public class SpotifyMusicProviderAdapter implements MusicRecommendationProvider {

    // Categoría de búsqueda adicional: query Spotify + género destino + popularidad máxima
    private record FallbackCategory(String query, MusicGenre genre, int maxPopularity) {}

    private static final List<FallbackCategory> EXTRA_FALLBACKS = List.of(
        // 1. Indie local — escena de sala pequeña
        new FallbackCategory("genre:\"indie\" year:2023-2026",          MusicGenre.INDIE,       35),
        // 2. Flamenco moderno — raíz española contemporánea
        new FallbackCategory("genre:\"flamenco\" year:2022-2026",       MusicGenre.FLAMENCO,    40),
        // 3. Spanish rock reciente — grupos emergentes
        new FallbackCategory("genre:\"spanish rock\" year:2022-2026",   MusicGenre.ROCK,        40),
        // 4. Trap en castellano — underground urbano
        new FallbackCategory("genre:\"trap\" year:2024-2026",           MusicGenre.TRAP,        40),
        // 5. Electronic nuevo — producciones recientes de baja notoriedad
        new FallbackCategory("genre:\"electronic\" year:2025-2026",     MusicGenre.ELECTRONIC,  30),
        // 6. House de club — sesiones de sala local
        new FallbackCategory("genre:\"house\" year:2024-2026",          MusicGenre.HOUSE,       35),
        // 7. Punk underground — escena de local pequeño
        new FallbackCategory("genre:\"punk\" year:2020-2026",           MusicGenre.PUNK,        30),
        // 8. Latin jazz — fusión emergente iberoamericana
        new FallbackCategory("genre:\"latin jazz\" year:2021-2026",     MusicGenre.LATIN_JAZZ,  25),
        // 9. Hip-hop en castellano — flows sin sello grande
        new FallbackCategory("genre:\"hip-hop\" year:2024-2026",        MusicGenre.HIP_HOP,     40),
        // 10. Reggaeton de nueva generación — no mainstream
        new FallbackCategory("genre:\"reggaeton\" year:2025-2026",      MusicGenre.REGGAETON,   45)
    );

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
                if (recommendations.size() >= 3) break;
                SpotifyDto.SpotifyTrackDTO track = fetchTopTrackForArtist(token, candidate.artistName());
                if (track != null) {
                    recommendations.add(
                            mapToDomain(track, genres)
                                    .withLabel(candidate.suggestedLabel())
                                    .withIsGresKArtist(true)
                    );
                    newlyShown.add(candidate.spotifyArtistId());
                }
            }

            if (recommendations.size() < 3) {
                for (MusicGenre genre : genres) {
                    if (recommendations.size() >= 3) break;
                    recommendations.addAll(findTracksByGenre(token, genre, city));
                }
            }

            // Nivel 3 — categorías extra: música local/española, underground, emergente
            if (recommendations.size() < 3) {
                log.info("nivel 3: usando {} categorías extra de fallback", EXTRA_FALLBACKS.size());
                for (FallbackCategory fb : EXTRA_FALLBACKS) {
                    if (recommendations.size() >= 3) break;
                    var resp = executeSearch(token, fb.query());
                    if (!isResponseEmpty(resp)) {
                        resp.tracks().items().stream()
                                .filter(t -> t.popularity() < fb.maxPopularity())
                                .limit(1)
                                .map(t -> mapToDomain(t, Set.of(fb.genre())).withLabel(RecommendationLabel.ESCENA_UNDERGROUND))
                                .forEach(recommendations::add);
                    }
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

    private SpotifyDto.SpotifyTrackDTO fetchTopTrackForArtist(String token, String artistName) {
        try {
            var response = apiClient.searchTracks(
                    token, "artist:\"" + artistName + "\"", "track", "ES", 5);
            if (isResponseEmpty(response)) return null;
            return response.tracks().items().get(0);
        } catch (Exception e) {
            log.warn("Failed to fetch top track for GresK artist {}: {}", artistName, e.getMessage());
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
                .limit(1)
                .map(t -> mapToDomain(t, Set.of(genre)).withLabel(RecommendationLabel.ACORDE_A_TUS_GUSTOS))
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

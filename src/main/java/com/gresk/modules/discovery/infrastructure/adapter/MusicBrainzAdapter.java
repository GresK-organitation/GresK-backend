package com.gresk.modules.discovery.infrastructure.adapter;

import com.gresk.modules.discovery.domain.port.out.MusicBrainzArtistInfo;
import com.gresk.modules.discovery.domain.port.out.MusicBrainzLookupPort;
import com.gresk.shared.infrastructure.musicbrainz.MusicBrainzApiClient;
import com.gresk.shared.infrastructure.musicbrainz.MusicBrainzConfig;
import com.gresk.shared.infrastructure.musicbrainz.MusicBrainzDto;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Enriquecimiento vía MusicBrainz (1 req/s obligatorio). Best-effort: si la
 * API falla o no encuentra coincidencia, el artista simplemente no se
 * enriquece — nunca bloquea el recálculo del perfil de Discovery.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class MusicBrainzAdapter implements MusicBrainzLookupPort {

    private final MusicBrainzApiClient apiClient;
    private final MusicBrainzConfig config;

    @Override
    @RateLimiter(name = "musicbrainz")
    @CircuitBreaker(name = "musicbrainz", fallbackMethod = "searchByNameFallback")
    public Optional<MusicBrainzArtistInfo> searchByName(String artistName) {
        MusicBrainzDto.ArtistSearchResponse response = apiClient.searchArtists(
                config.getUserAgent(), "artist:" + artistName, "json", 1);

        if (response == null || response.artists() == null || response.artists().isEmpty()) {
            return Optional.empty();
        }

        MusicBrainzDto.ArtistResult result = response.artists().get(0);
        Integer beginYear = parseBeginYear(result.lifeSpan());

        return Optional.of(new MusicBrainzArtistInfo(
                result.id(),
                result.country(),
                result.area() != null ? result.area().name() : null,
                beginYear
        ));
    }

    private Optional<MusicBrainzArtistInfo> searchByNameFallback(String artistName, Throwable t) {
        log.warn("MusicBrainz lookup failed for '{}': {}", artistName, t.getMessage());
        return Optional.empty();
    }

    private Integer parseBeginYear(MusicBrainzDto.LifeSpan lifeSpan) {
        if (lifeSpan == null || lifeSpan.begin() == null || lifeSpan.begin().isBlank()) return null;
        try {
            return Integer.parseInt(lifeSpan.begin().substring(0, 4));
        } catch (NumberFormatException e) {
            return null;
        }
    }
}

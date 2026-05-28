package com.gresk.modules.artist.infrastructure.adapters.spotify;

import com.gresk.modules.artist.application.dto.SpotifyArtistSuggestionDTO;
import com.gresk.modules.artist.domain.port.out.SpotifyArtistSearchPort;
import com.gresk.shared.infrastructure.spotify.SpotifyApiClient;
import com.gresk.shared.infrastructure.spotify.SpotifyDto;
import com.gresk.shared.infrastructure.spotify.SpotifyTokenService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Adaptador de infraestructura que implementa SpotifyArtistSearchPort.
 * Llama a la Spotify Web API para buscar artistas por nombre y mapea
 * la respuesta al DTO de aplicación.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class SpotifyArtistSearchAdapter implements SpotifyArtistSearchPort {

    private final SpotifyApiClient    apiClient;
    private final SpotifyTokenService tokenService;

    @Override
    public List<SpotifyArtistSuggestionDTO> searchByName(String name, int limit) {
        try {
            String token = tokenService.getBearerToken();

            SpotifyDto.SpotifyArtistSearchResponse response =
                    apiClient.searchArtists(token, name, "artist", limit);

            if (response == null || response.artists() == null
                    || response.artists().items() == null) {
                return List.of();
            }

            return response.artists().items().stream()
                    .map(this::toDTO)
                    .toList();

        } catch (Exception e) {
            log.error("Spotify artist search failed for name '{}': {}", name, e.getMessage());
            return List.of();
        }
    }

    private SpotifyArtistSuggestionDTO toDTO(SpotifyDto.SpotifyArtistResultDTO artist) {
        String imageUrl = (artist.images() != null && !artist.images().isEmpty())
                ? artist.images().get(0).url()
                : null;

        int followers = (artist.followers() != null) ? artist.followers().total() : 0;

        return new SpotifyArtistSuggestionDTO(
                artist.id(),
                artist.name(),
                imageUrl,
                artist.genres() != null ? artist.genres() : List.of(),
                artist.popularity(),
                followers
        );
    }
}

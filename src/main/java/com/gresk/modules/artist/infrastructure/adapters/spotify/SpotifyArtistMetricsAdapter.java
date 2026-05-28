package com.gresk.modules.artist.infrastructure.adapters.spotify;

import com.gresk.modules.artist.application.dto.SpotifyArtistMetricsDTO;
import com.gresk.modules.artist.domain.port.out.SpotifyArtistMetricsPort;
import com.gresk.shared.infrastructure.spotify.SpotifyApiClient;
import com.gresk.shared.infrastructure.spotify.SpotifyDto;
import com.gresk.shared.infrastructure.spotify.SpotifyTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.YearMonth;

@Component
@RequiredArgsConstructor
public class SpotifyArtistMetricsAdapter implements SpotifyArtistMetricsPort {

    private final SpotifyApiClient   apiClient;
    private final SpotifyTokenService tokenService;

    @Override
    public SpotifyArtistMetricsDTO fetchMetrics(String spotifyArtistId) {
        String token = tokenService.getBearerToken();

        SpotifyDto.ArtistDetailResponse detail = apiClient.getArtistDetail(token, spotifyArtistId);
        SpotifyDto.AlbumSearchResponse  albums = apiClient.getLatestAlbums(token, spotifyArtistId, 1, "album,single");

        return new SpotifyArtistMetricsDTO(
                detail.popularity(),
                detail.followers().total(),
                parseReleaseDate(albums),
                albums.total()
        );
    }

    private LocalDate parseReleaseDate(SpotifyDto.AlbumSearchResponse albums) {
        if (albums.items().isEmpty()) return null;
        SpotifyDto.AlbumItemDTO item = albums.items().get(0);
        if (item.releaseDate() == null || item.releaseDate().isBlank()) return null;
        return switch (item.releaseDatePrecision()) {
            case "day"   -> LocalDate.parse(item.releaseDate());
            case "month" -> YearMonth.parse(item.releaseDate()).atDay(1);
            case "year"  -> LocalDate.of(Integer.parseInt(item.releaseDate()), 1, 1);
            default      -> null;
        };
    }
}

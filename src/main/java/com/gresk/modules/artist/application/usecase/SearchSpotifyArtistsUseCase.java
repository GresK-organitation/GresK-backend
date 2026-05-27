package com.gresk.modules.artist.application.usecase;

import com.gresk.modules.artist.application.dto.SpotifyArtistSuggestionDTO;
import com.gresk.modules.artist.application.port.in.SearchSpotifyArtistsPort;
import com.gresk.modules.artist.domain.port.out.SpotifyArtistSearchPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SearchSpotifyArtistsUseCase implements SearchSpotifyArtistsPort {

    private static final int MAX_RESULTS = 5;

    private final SpotifyArtistSearchPort spotifyArtistSearchPort;

    @Override
    public List<SpotifyArtistSuggestionDTO> execute(String name) {
        return spotifyArtistSearchPort.searchByName(name, MAX_RESULTS);
    }
}

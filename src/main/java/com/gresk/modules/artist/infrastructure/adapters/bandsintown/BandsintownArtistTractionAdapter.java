package com.gresk.modules.artist.infrastructure.adapters.bandsintown;

import com.gresk.modules.artist.application.dto.BandsintownTractionDTO;
import com.gresk.modules.artist.domain.port.out.BandsintownArtistTractionPort;
import com.gresk.shared.infrastructure.bandsintown.BandsintownApiClient;
import com.gresk.shared.infrastructure.bandsintown.BandsintownConfig;
import com.gresk.shared.infrastructure.bandsintown.BandsintownDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
public class BandsintownArtistTractionAdapter implements BandsintownArtistTractionPort {

    private final BandsintownApiClient apiClient;
    private final BandsintownConfig    config;

    @Override
    public Optional<BandsintownTractionDTO> fetchTraction(String artistName) {
        try {
            BandsintownDto.ArtistResponse response = apiClient.getArtist(artistName, config.getAppId());
            return Optional.of(new BandsintownTractionDTO(
                    parseIntOrNull(response.tracker_count()),
                    parseIntOrNull(response.upcoming_event_count())
            ));
        } catch (Exception e) {
            log.warn("Bandsintown lookup failed for artist '{}': {}", artistName, e.getMessage());
            return Optional.empty();
        }
    }

    private Integer parseIntOrNull(String value) {
        try {
            return value != null ? Integer.parseInt(value) : null;
        } catch (NumberFormatException e) {
            return null;
        }
    }
}

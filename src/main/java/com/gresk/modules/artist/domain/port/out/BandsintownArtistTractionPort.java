package com.gresk.modules.artist.domain.port.out;

import com.gresk.modules.artist.application.dto.BandsintownTractionDTO;

import java.util.Optional;

/**
 * Puerto de dominio hacia la integración Bandsintown, análogo a
 * SpotifyArtistMetricsPort.
 */
public interface BandsintownArtistTractionPort {
    Optional<BandsintownTractionDTO> fetchTraction(String artistName);
}

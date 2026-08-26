package com.gresk.modules.discovery.application.usecase;

import com.gresk.modules.artist.domain.model.valueobject.ArtistId;
import com.gresk.modules.discovery.domain.port.out.ArtistCatalogPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Recalcula el perfil de descubrimiento de todos los artistas del catálogo.
 * Un fallo aislado no bloquea el resto del batch — mismo criterio que
 * RecalculateStaleMusicDnaUseCase.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RecalculateAllDiscoveryProfilesUseCase {

    private final ArtistCatalogPort artistCatalogPort;
    private final RecalculateArtistDiscoveryProfileUseCase recalculateArtistDiscoveryProfileUseCase;

    public void execute() {
        for (ArtistId artistId : artistCatalogPort.findAllArtistIds()) {
            try {
                recalculateArtistDiscoveryProfileUseCase.execute(artistId);
            } catch (Exception e) {
                log.error("Failed to recalculate discovery profile for artist {}: {}", artistId, e.getMessage());
            }
        }
    }
}

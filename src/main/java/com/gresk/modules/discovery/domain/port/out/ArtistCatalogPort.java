package com.gresk.modules.discovery.domain.port.out;

import com.gresk.modules.artist.domain.model.valueobject.ArtistId;

import java.util.List;
import java.util.Optional;

/**
 * Lookup de solo lectura al catálogo de artistas del módulo `artist`.
 * Nunca escribe, nunca toca las métricas oficiales del artista — mismo
 * criterio que {@code journal.domain.port.out.ArtistLookupPort}.
 */
public interface ArtistCatalogPort {
    Optional<ArtistCatalogInfo> findById(ArtistId artistId);
    List<ArtistId> findAllArtistIds();
}

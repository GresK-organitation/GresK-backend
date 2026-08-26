package com.gresk.modules.discovery.domain.port.out;

import com.gresk.modules.artist.domain.model.valueobject.ArtistId;

/**
 * Agrega señales de comunidad GresK para un artista (reseñas, asistentes
 * verificados, usuarios que lo conocen) a partir de `reviews`, `journal_entries`
 * y `artist_demand_signals`. Consulta cross-módulo de solo lectura.
 */
public interface CommunitySignalsPort {
    CommunitySignals findSignals(ArtistId artistId);
}

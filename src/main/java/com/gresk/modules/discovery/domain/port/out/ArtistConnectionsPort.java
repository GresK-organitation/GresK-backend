package com.gresk.modules.discovery.domain.port.out;

import com.gresk.modules.artist.domain.model.valueobject.ArtistId;

import java.util.List;

/**
 * Modo "Conexiones": artistas relacionados por co-reseña — usuarios que
 * reseñaron el artista X también reseñaron el artista Y. Consulta cross-módulo
 * de solo lectura (reviews→events, agrupada por artista).
 */
public interface ArtistConnectionsPort {
    List<ArtistConnection> findConnections(ArtistId artistId, int limit);
}

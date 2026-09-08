package com.gresk.modules.artist.domain.port.out;

import com.gresk.modules.artist.domain.model.valueobject.ArtistId;

import java.util.List;

/**
 * Puerto de solo lectura hacia event/ticket/contract para construir el
 * histórico de rendimiento de giras de un artista. No persiste datos propios
 * — mismo patrón que EventCatalogPort en el módulo discovery.
 */
public interface ArtistTourHistoryPort {
    List<TourPerformanceRecord> findPastPerformances(ArtistId artistId);
}

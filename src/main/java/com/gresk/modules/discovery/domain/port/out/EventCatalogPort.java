package com.gresk.modules.discovery.domain.port.out;

import com.gresk.modules.artist.domain.model.valueobject.ArtistId;

import java.util.Optional;

/** Lookup de solo lectura al módulo `event` para la actividad en vivo de un artista. */
public interface EventCatalogPort {
    Optional<UpcomingEventInfo> findNextUpcomingEvent(ArtistId artistId);
    boolean hasEverHadEvents(ArtistId artistId);
}

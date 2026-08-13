package com.gresk.modules.journal.application.usecase;

import com.gresk.modules.artist.domain.model.valueobject.ArtistId;
import com.gresk.modules.event.domain.model.EventId;
import com.gresk.modules.journal.domain.exception.InvalidJournalEntryException;
import com.gresk.modules.journal.domain.port.out.ArtistLookupPort;
import com.gresk.modules.journal.domain.port.out.EventLookupPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Resolves the optional, display-only catalog links a JournalEntry may carry
 * (artist/event), validating existence without ever writing back to those modules.
 */
@Component
@RequiredArgsConstructor
class CatalogLinkResolver {

    private final ArtistLookupPort artistLookupPort;
    private final EventLookupPort  eventLookupPort;

    ArtistId resolveArtistId(String artistId) {
        if (artistId == null || artistId.isBlank()) return null;
        ArtistId id = ArtistId.of(artistId);
        if (!artistLookupPort.existsById(id)) {
            throw new InvalidJournalEntryException("Artist not found: " + artistId);
        }
        return id;
    }

    EventId resolveEventId(String eventId) {
        if (eventId == null || eventId.isBlank()) return null;
        EventId id = EventId.of(eventId);
        if (!eventLookupPort.existsById(id)) {
            throw new InvalidJournalEntryException("Event not found: " + eventId);
        }
        return id;
    }
}

package com.gresk.modules.journal.domain.port.out;

import com.gresk.modules.artist.domain.model.valueobject.ArtistId;

import java.util.Optional;

/**
 * Read-only lookup into the artist catalog. Used only to validate/display an
 * optional link on a JournalEntry — never writes back, never touches
 * official rating aggregates.
 */
public interface ArtistLookupPort {
    boolean existsById(ArtistId id);
    Optional<String> findNameById(ArtistId id);
}

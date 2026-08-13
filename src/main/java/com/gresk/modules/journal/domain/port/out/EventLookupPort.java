package com.gresk.modules.journal.domain.port.out;

import com.gresk.modules.event.domain.model.EventId;

/**
 * Read-only lookup into the event catalog. Used only to validate/display an
 * optional link on a JournalEntry — never writes back, never touches
 * official rating aggregates (that stays exclusive to review.EventRatingPort).
 */
public interface EventLookupPort {
    boolean existsById(EventId id);
}

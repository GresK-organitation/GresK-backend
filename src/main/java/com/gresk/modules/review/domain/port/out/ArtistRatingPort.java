package com.gresk.modules.review.domain.port.out;

import com.gresk.modules.event.domain.model.EventId;

/**
 * Output port: after a review is submitted or updated, recalculates
 * the average artist rating on the linked Artist aggregate (if any).
 * Uses only the eventId to keep cross-module coupling minimal.
 */
public interface ArtistRatingPort {
    void recalculateForEvent(EventId eventId);
}

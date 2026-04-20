package com.gresk.modules.review.domain.port.out;

import com.gresk.modules.event.domain.model.EventId;

/**
 * Output port: update the event's running average ratings after a review
 * is submitted or edited. Implemented in the event infrastructure layer.
 *
 * Primitive int values are used to avoid cross-module domain coupling.
 */
public interface EventRatingPort {
    void addRating(EventId eventId,
                   int artist, int sound, int ambience,
                   int venue, int setlist, int overall);

    /**
     * Replaces the event's rating stats with a freshly computed set.
     * Used after a review is edited to recalculate from all current reviews.
     */
    void setStats(EventId eventId, int reviewCount,
                  double avgOverall, double avgArtist, double avgSound,
                  double avgAmbience, double avgVenue, double avgSetlist);
}

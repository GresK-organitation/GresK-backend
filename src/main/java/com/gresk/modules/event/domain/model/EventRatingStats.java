package com.gresk.modules.event.domain.model;

/**
 * Denormalized community rating stats for an event.
 * Updated incrementally on each review submission using Welford's formula.
 * Stored as flat columns in the events table.
 */
public record EventRatingStats(
        int    reviewCount,
        double avgOverallRating,
        double avgArtistRating,
        double avgSoundRating,
        double avgAmbienceRating,
        double avgVenueRating,
        double avgSetlistRating
) {
    public static EventRatingStats empty() {
        return new EventRatingStats(0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0);
    }

    /**
     * Returns a new instance with the given review folded in
     * using Welford's incremental average formula.
     */
    public EventRatingStats withNewRating(int artist, int sound, int ambience,
                                          int venue, int setlist, int overall) {
        int n = reviewCount + 1;
        return new EventRatingStats(
            n,
            incrementalAvg(avgOverallRating,  overall,  n),
            incrementalAvg(avgArtistRating,   artist,   n),
            incrementalAvg(avgSoundRating,    sound,    n),
            incrementalAvg(avgAmbienceRating, ambience, n),
            incrementalAvg(avgVenueRating,    venue,    n),
            incrementalAvg(avgSetlistRating,  setlist,  n)
        );
    }

    private static double incrementalAvg(double currentAvg, int newValue, int newCount) {
        return ((currentAvg * (newCount - 1)) + newValue) / newCount;
    }
}

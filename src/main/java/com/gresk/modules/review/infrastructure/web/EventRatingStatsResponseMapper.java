package com.gresk.modules.review.infrastructure.web;

import com.gresk.modules.event.domain.model.EventRatingStats;
import org.springframework.stereotype.Component;

@Component
public class EventRatingStatsResponseMapper {

    public EventRatingStatsResponse toResponse(EventRatingStats stats) {
        return new EventRatingStatsResponse(
                stats.reviewCount(),
                stats.avgOverallRating(),
                stats.avgArtistRating(),
                stats.avgSoundRating(),
                stats.avgAmbienceRating(),
                stats.avgVenueRating(),
                stats.avgSetlistRating()
        );
    }
}

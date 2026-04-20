package com.gresk.modules.review.infrastructure.web;

import com.gresk.modules.review.domain.model.Review;
import org.springframework.stereotype.Component;

@Component
public class ReviewResponseMapper {

    public ReviewResponse toResponse(Review review) {
        return new ReviewResponse(
                review.getId().value().toString(),
                review.getEventId().value().toString(),
                review.getTicketId().value().toString(),
                review.getDetailedRating().artistRating().value(),
                review.getDetailedRating().soundRating().value(),
                review.getDetailedRating().ambienceRating().value(),
                review.getDetailedRating().venueRating().value(),
                review.getDetailedRating().setlistRating().value(),
                review.getOverallRating().value(),
                review.getComment().isEmpty() ? null : review.getComment().value(),
                review.getPhotoUrl() != null ? review.getPhotoUrl().value() : null,
                review.getPointsAwarded(),
                review.getStatus().name(),
                review.getCreatedAt().toString()
        );
    }
}

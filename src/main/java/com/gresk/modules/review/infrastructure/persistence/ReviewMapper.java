package com.gresk.modules.review.infrastructure.persistence;

import com.gresk.modules.event.domain.model.EventId;
import com.gresk.modules.review.domain.model.*;
import com.gresk.modules.ticket.domain.model.TicketId;
import com.gresk.modules.user.domain.model.UserId;
import com.gresk.shared.domain.valueobject.ImageUrl;
import org.springframework.stereotype.Component;

@Component
public class ReviewMapper {

    public Review toDomain(ReviewEntity entity) {
        DetailedRating detailedRating = DetailedRating.reconstitute(
                entity.getArtistRating(),
                entity.getSoundRating(),
                entity.getAmbienceRating(),
                entity.getVenueRating(),
                entity.getSetlistRating()
        );

        ImageUrl photoUrl = (entity.getPhotoUrl() != null && !entity.getPhotoUrl().isBlank())
                ? ImageUrl.reconstitute(entity.getPhotoUrl()) : null;

        return Review.reconstitute(
                ReviewId.of(entity.getId()),
                UserId.of(entity.getUserId()),
                new EventId(entity.getEventId()),
                new TicketId(entity.getTicketId()),
                detailedRating,
                Rating.reconstitute(entity.getOverallRating()),
                ReviewComment.reconstitute(entity.getComment()),
                photoUrl,
                entity.getPointsAwarded(),
                entity.getStatus(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }

    public ReviewEntity toEntity(Review review) {
        return ReviewEntity.builder()
                .id(review.getId().value())
                .userId(review.getUserId().value())
                .eventId(review.getEventId().value())
                .ticketId(review.getTicketId().value())
                .artistRating(review.getDetailedRating().artistRating().value())
                .soundRating(review.getDetailedRating().soundRating().value())
                .ambienceRating(review.getDetailedRating().ambienceRating().value())
                .venueRating(review.getDetailedRating().venueRating().value())
                .setlistRating(review.getDetailedRating().setlistRating().value())
                .overallRating(review.getOverallRating().value())
                .comment(review.getComment().isEmpty() ? null : review.getComment().value())
                .photoUrl(review.getPhotoUrl() != null ? review.getPhotoUrl().value() : null)
                .pointsAwarded(review.getPointsAwarded())
                .status(review.getStatus())
                .createdAt(review.getCreatedAt())
                .updatedAt(review.getUpdatedAt())
                .build();
    }
}

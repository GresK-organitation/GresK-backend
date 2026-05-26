package com.gresk.modules.review.infrastructure.persistence;

import com.gresk.modules.event.domain.model.EventId;
import com.gresk.modules.review.domain.model.*;
import com.gresk.modules.ticket.domain.model.TicketId;
import com.gresk.modules.user.domain.model.UserId;
import com.gresk.shared.domain.valueobject.ImageUrl;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class ReviewMapper {

    /**
     * Reconstitutes a Review WITHOUT loading the likedBy collection.
     * Use for list queries (findByEventId, findByUserId) where like data
     * is provided separately via batch queries — avoids N+1.
     */
    public Review toDomain(ReviewEntity entity) {
        return reconstitute(entity, new HashSet<>());
    }

    /**
     * Reconstitutes a Review WITH its likedBy collection loaded from the entity.
     * Use for single-entity write operations (findById) where the full set is
     * needed to enforce domain invariants (addLike duplicate check).
     * Triggers one lazy SELECT — acceptable on single-entity write paths.
     */
    public Review toDomainWithLikes(ReviewEntity entity) {
        Set<UserId> likedBy = entity.getLikedBy().stream()
                .map(UserId::of)
                .collect(Collectors.toSet());
        return reconstitute(entity, likedBy);
    }

    private Review reconstitute(ReviewEntity entity, Set<UserId> likedBy) {
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
                entity.getUpdatedAt(),
                likedBy
        );
    }

    public ReviewEntity toEntity(Review review) {
        Set<UUID> likedByUuids = review.getLikedBy().stream()
                .map(UserId::value)
                .collect(Collectors.toSet());

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
                .likedBy(likedByUuids)
                .build();
    }
}

package com.gresk.modules.review.infrastructure.web;

import com.gresk.modules.review.application.usecase.ReviewWithLikeContext;
import com.gresk.modules.review.domain.model.Review;
import com.gresk.modules.user.domain.model.UserId;
import org.springframework.stereotype.Component;

@Component
public class ReviewResponseMapper {

    /**
     * For write operations (submit, update, addLike, removeLike):
     * the likedBy set is already in memory — safe to use getLikedBy() directly.
     */
    public ReviewResponse toResponse(Review review, String currentUserId) {
        boolean likedByCurrentUser = review.getLikedBy()
                .contains(UserId.from(currentUserId));

        return build(review, review.getLikeCount(), likedByCurrentUser);
    }

    /**
     * For list read operations (getEventReviews, getUserReviews):
     * like data comes from pre-computed batch queries — avoids triggering
     * the lazy likedBy collection per review (N+1 prevention).
     */
    public ReviewResponse toResponse(ReviewWithLikeContext context) {
        return build(context.review(), context.likeCount(), context.likedByCurrentUser());
    }

    private ReviewResponse build(Review review, int likeCount, boolean likedByCurrentUser) {
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
                review.getCreatedAt().toString(),
                likeCount,
                likedByCurrentUser
        );
    }
}

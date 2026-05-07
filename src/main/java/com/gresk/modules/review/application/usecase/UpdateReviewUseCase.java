package com.gresk.modules.review.application.usecase;

import com.gresk.modules.event.domain.model.EventId;
import com.gresk.modules.review.application.port.in.UpdateReviewPort;
import com.gresk.modules.review.domain.exception.ReviewForbiddenException;
import com.gresk.modules.review.domain.exception.ReviewNotFoundException;
import com.gresk.modules.review.domain.model.DetailedRating;
import com.gresk.modules.review.domain.model.Review;
import com.gresk.modules.review.domain.model.ReviewComment;
import com.gresk.modules.review.domain.model.ReviewId;
import com.gresk.modules.review.domain.port.out.ArtistRatingPort;
import com.gresk.modules.review.domain.port.out.EventRatingPort;
import com.gresk.modules.review.domain.port.out.ReviewRepository;
import com.gresk.modules.user.domain.model.UserId;
import com.gresk.shared.domain.valueobject.ImageUrl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UpdateReviewUseCase implements UpdateReviewPort {

    private final ReviewRepository reviewRepository;
    private final EventRatingPort  eventRatingPort;
    private final ArtistRatingPort artistRatingPort;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Review execute(UpdateReviewCommand command) {
        ReviewId reviewId = ReviewId.of(command.reviewId());
        UserId   userId   = UserId.from(command.userId());

        // 1. Load and validate ownership
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ReviewNotFoundException(
                        "Review not found: " + command.reviewId()));

        if (!review.getUserId().equals(userId)) {
            throw new ReviewForbiddenException("Review does not belong to this user");
        }

        EventId eventId = review.getEventId();

        // 2. Update the aggregate
        DetailedRating newRating = DetailedRating.of(
                command.artistRating(),   command.soundRating(),
                command.ambienceRating(), command.venueRating(),
                command.setlistRating()
        );
        ReviewComment newComment  = ReviewComment.of(command.comment());
        ImageUrl      newPhotoUrl = (command.photoUrl() != null && !command.photoUrl().isBlank())
                ? ImageUrl.of(command.photoUrl()) : null;

        review.update(newRating, newComment, newPhotoUrl);
        reviewRepository.save(review);

        // 3. Recalculate event community stats from all current reviews
        List<Review> allReviews = reviewRepository.findByEventId(eventId);
        int    count       = allReviews.size();
        double avgOverall  = avg(allReviews, r -> r.getOverallRating().value());
        double avgArtist   = avg(allReviews, r -> r.getDetailedRating().artistRating().value());
        double avgSound    = avg(allReviews, r -> r.getDetailedRating().soundRating().value());
        double avgAmbience = avg(allReviews, r -> r.getDetailedRating().ambienceRating().value());
        double avgVenue    = avg(allReviews, r -> r.getDetailedRating().venueRating().value());
        double avgSetlist  = avg(allReviews, r -> r.getDetailedRating().setlistRating().value());

        eventRatingPort.setStats(eventId, count,
                avgOverall, avgArtist, avgSound, avgAmbience, avgVenue, avgSetlist);

        // Recalcular avgRating en el Artist vinculado (sólo artist_rating)
        artistRatingPort.recalculateForEvent(eventId);

        return review;
    }

    @FunctionalInterface
    private interface RatingExtractor {
        int extract(Review r);
    }

    private double avg(List<Review> reviews, RatingExtractor extractor) {
        return reviews.stream()
                .mapToInt(extractor::extract)
                .average()
                .orElse(0.0);
    }
}

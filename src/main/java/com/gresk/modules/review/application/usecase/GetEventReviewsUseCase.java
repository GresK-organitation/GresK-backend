package com.gresk.modules.review.application.usecase;

import com.gresk.modules.event.domain.model.EventId;
import com.gresk.modules.review.domain.model.Review;
import com.gresk.modules.review.domain.model.ReviewId;
import com.gresk.modules.review.domain.port.out.ReviewRepository;
import com.gresk.modules.user.domain.model.UserId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class GetEventReviewsUseCase {

    private final ReviewRepository reviewRepository;

    @Transactional(readOnly = true)
    public List<ReviewWithLikeContext> execute(String eventId, String currentUserId) {
        List<Review> reviews = reviewRepository.findByEventId(EventId.of(eventId));
        if (reviews.isEmpty()) return List.of();

        List<ReviewId> ids      = reviews.stream().map(Review::getId).toList();
        UserId         userId   = UserId.from(currentUserId);

        // Two queries instead of N+1: one COUNT per review, one EXISTS per review+user
        Map<ReviewId, Integer> likeCounts  = reviewRepository.countLikesByReviewIds(ids);
        Set<ReviewId>          likedByUser = reviewRepository.findLikedReviewIdsByUser(ids, userId);

        return reviews.stream()
                .map(r -> new ReviewWithLikeContext(
                        r,
                        likeCounts.getOrDefault(r.getId(), 0),
                        likedByUser.contains(r.getId())
                ))
                .toList();
    }
}

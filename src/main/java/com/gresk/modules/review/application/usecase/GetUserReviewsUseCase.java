package com.gresk.modules.review.application.usecase;

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
public class GetUserReviewsUseCase {

    private final ReviewRepository reviewRepository;

    @Transactional(readOnly = true)
    public List<ReviewWithLikeContext> execute(GetUserReviewsQuery query) {
        UserId         userId  = UserId.from(query.userId());
        List<Review>   reviews = reviewRepository.findByUserId(userId);
        if (reviews.isEmpty()) return List.of();

        List<ReviewId> ids = reviews.stream().map(Review::getId).toList();

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

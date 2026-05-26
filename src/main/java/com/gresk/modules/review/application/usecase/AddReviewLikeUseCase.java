package com.gresk.modules.review.application.usecase;

import com.gresk.modules.review.application.port.in.AddReviewLikePort;
import com.gresk.modules.review.domain.exception.ReviewNotFoundException;
import com.gresk.modules.review.domain.model.Review;
import com.gresk.modules.review.domain.model.ReviewId;
import com.gresk.modules.review.domain.port.out.ReviewRepository;
import com.gresk.modules.user.domain.model.UserId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AddReviewLikeUseCase implements AddReviewLikePort {

    private final ReviewRepository reviewRepository;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Review execute(AddReviewLikeCommand command) {
        ReviewId reviewId = ReviewId.of(command.reviewId());
        UserId   userId   = UserId.from(command.userId());

        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ReviewNotFoundException(
                        "Review not found: " + command.reviewId()));

        review.addLike(userId); // el dominio lanza ReviewAlreadyLikedException si ya existe
        reviewRepository.save(review);
        return review; // devolvemos el objeto en memoria: likedBy ya está actualizado correctamente
    }
}

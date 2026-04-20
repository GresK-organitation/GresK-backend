package com.gresk.modules.review.application.usecase;

import com.gresk.modules.review.domain.model.Review;
import com.gresk.modules.review.domain.port.out.ReviewRepository;
import com.gresk.modules.user.domain.model.UserId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GetUserReviewsUseCase {

    private final ReviewRepository reviewRepository;

    @Transactional(readOnly = true)
    public List<Review> execute(GetUserReviewsQuery query) {
        return reviewRepository.findByUserId(UserId.from(query.userId()));
    }
}

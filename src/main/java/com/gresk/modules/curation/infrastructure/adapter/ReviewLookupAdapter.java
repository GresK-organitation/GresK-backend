package com.gresk.modules.curation.infrastructure.adapter;

import com.gresk.modules.curation.domain.port.out.ReviewLookupPort;
import com.gresk.modules.review.domain.model.ReviewId;
import com.gresk.modules.review.domain.port.out.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ReviewLookupAdapter implements ReviewLookupPort {

    private final ReviewRepository reviewRepository;

    @Override
    public boolean existsById(UUID reviewId) {
        return reviewRepository.findById(ReviewId.of(reviewId)).isPresent();
    }
}

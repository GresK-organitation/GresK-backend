package com.gresk.modules.review.application.usecase;

import com.gresk.modules.event.domain.model.EventId;
import com.gresk.modules.review.domain.model.Review;
import com.gresk.modules.review.domain.port.out.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GetEventReviewsUseCase {

    private final ReviewRepository reviewRepository;

    @Transactional(readOnly = true)
    public List<Review> execute(String eventId) {
        return reviewRepository.findByEventId(EventId.of(eventId));
    }
}

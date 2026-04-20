package com.gresk.modules.review.infrastructure;

import com.gresk.modules.review.application.usecase.SubmitReviewUseCase;
import com.gresk.modules.review.application.usecase.UpdateReviewUseCase;
import com.gresk.modules.review.domain.port.out.EventRatingPort;
import com.gresk.modules.review.domain.port.out.ReviewRepository;
import com.gresk.modules.review.domain.port.out.UserPointsPort;
import com.gresk.modules.ticket.domain.port.out.TicketRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ReviewModuleConfiguration {

    @Bean
    public SubmitReviewUseCase submitReviewUseCase(ReviewRepository reviewRepository,
                                                   TicketRepository ticketRepository,
                                                   UserPointsPort userPointsPort,
                                                   EventRatingPort eventRatingPort) {
        return new SubmitReviewUseCase(
                reviewRepository, ticketRepository,
                userPointsPort, eventRatingPort);
    }

    @Bean
    public UpdateReviewUseCase updateReviewUseCase(ReviewRepository reviewRepository,
                                                   EventRatingPort eventRatingPort) {
        return new UpdateReviewUseCase(reviewRepository, eventRatingPort);
    }
}

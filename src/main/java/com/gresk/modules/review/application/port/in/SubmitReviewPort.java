package com.gresk.modules.review.application.port.in;

import com.gresk.modules.review.application.usecase.SubmitReviewCommand;
import com.gresk.modules.review.domain.model.Review;

public interface SubmitReviewPort {
    Review execute(SubmitReviewCommand command);
}

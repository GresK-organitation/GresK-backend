package com.gresk.modules.review.application.port.in;

import com.gresk.modules.review.application.usecase.UpdateReviewCommand;
import com.gresk.modules.review.domain.model.Review;

public interface UpdateReviewPort {
    Review execute(UpdateReviewCommand command);
}

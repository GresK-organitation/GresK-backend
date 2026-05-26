package com.gresk.modules.review.application.port.in;

import com.gresk.modules.review.application.usecase.AddReviewLikeCommand;
import com.gresk.modules.review.domain.model.Review;

public interface AddReviewLikePort {
    Review execute(AddReviewLikeCommand command);
}

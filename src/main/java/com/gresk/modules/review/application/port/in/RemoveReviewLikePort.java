package com.gresk.modules.review.application.port.in;

import com.gresk.modules.review.application.usecase.RemoveReviewLikeCommand;
import com.gresk.modules.review.domain.model.Review;

public interface RemoveReviewLikePort {
    Review execute(RemoveReviewLikeCommand command);
}

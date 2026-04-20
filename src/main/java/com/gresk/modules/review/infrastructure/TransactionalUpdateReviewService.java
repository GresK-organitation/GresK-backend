package com.gresk.modules.review.infrastructure;

import com.gresk.modules.review.application.usecase.UpdateReviewCommand;
import com.gresk.modules.review.application.usecase.UpdateReviewUseCase;
import com.gresk.modules.review.domain.model.Review;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TransactionalUpdateReviewService {

    private final UpdateReviewUseCase updateReviewUseCase;

    @Transactional(rollbackFor = Exception.class)
    public Review execute(UpdateReviewCommand command) {
        return updateReviewUseCase.execute(command);
    }
}

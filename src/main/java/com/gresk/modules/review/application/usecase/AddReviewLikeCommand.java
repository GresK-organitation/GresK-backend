package com.gresk.modules.review.application.usecase;

public record AddReviewLikeCommand(
        String reviewId,
        String userId
) {}

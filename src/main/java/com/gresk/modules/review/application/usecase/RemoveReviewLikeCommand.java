package com.gresk.modules.review.application.usecase;

public record RemoveReviewLikeCommand(
        String reviewId,
        String userId
) {}

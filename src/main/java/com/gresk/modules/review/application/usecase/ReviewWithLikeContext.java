package com.gresk.modules.review.application.usecase;

import com.gresk.modules.review.domain.model.Review;

/**
 * Read-only projection that enriches a Review with pre-computed like data.
 * Used by list use cases to avoid loading the full likedBy collection
 * per review (N+1 prevention). Write operations use Review directly
 * since the collection is already in memory after the domain operation.
 */
public record ReviewWithLikeContext(
        Review  review,
        int     likeCount,
        boolean likedByCurrentUser
) {}

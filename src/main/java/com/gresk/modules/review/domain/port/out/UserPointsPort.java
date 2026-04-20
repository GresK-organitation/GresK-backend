package com.gresk.modules.review.domain.port.out;

import com.gresk.modules.user.domain.model.UserId;

/**
 * Output port: grant loyalty points to a user after a review is submitted.
 * Implemented in the user infrastructure layer.
 */
public interface UserPointsPort {
    void addPoints(UserId userId, int points);
}

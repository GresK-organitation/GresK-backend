package com.gresk.modules.review.domain.port.out;

import com.gresk.modules.event.domain.model.EventId;
import com.gresk.modules.review.domain.model.Review;
import com.gresk.modules.review.domain.model.ReviewId;
import com.gresk.modules.ticket.domain.model.TicketId;
import com.gresk.modules.user.domain.model.UserId;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public interface ReviewRepository {
    Review save(Review review);
    Optional<Review> findById(ReviewId id);
    Optional<Review> findByTicketId(TicketId ticketId);
    List<Review> findByUserId(UserId userId);
    List<Review> findByEventId(EventId eventId);
    boolean existsByTicketId(TicketId ticketId);

    // ── Batch like queries (for list responses — avoids N+1) ─────────────────
    Map<ReviewId, Integer> countLikesByReviewIds(List<ReviewId> ids);
    Set<ReviewId> findLikedReviewIdsByUser(List<ReviewId> ids, UserId userId);
}

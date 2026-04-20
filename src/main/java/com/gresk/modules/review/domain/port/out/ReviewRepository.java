package com.gresk.modules.review.domain.port.out;

import com.gresk.modules.event.domain.model.EventId;
import com.gresk.modules.review.domain.model.Review;
import com.gresk.modules.review.domain.model.ReviewId;
import com.gresk.modules.ticket.domain.model.TicketId;
import com.gresk.modules.user.domain.model.UserId;

import java.util.List;
import java.util.Optional;

public interface ReviewRepository {
    Review save(Review review);
    Optional<Review> findById(ReviewId id);
    Optional<Review> findByTicketId(TicketId ticketId);
    List<Review> findByUserId(UserId userId);
    List<Review> findByEventId(EventId eventId);
    boolean existsByTicketId(TicketId ticketId);
}

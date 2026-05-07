package com.gresk.modules.review.application.usecase;

import com.gresk.modules.event.domain.model.EventId;
import com.gresk.modules.review.application.port.in.SubmitReviewPort;
import com.gresk.modules.review.domain.exception.ReviewAlreadyExistsException;
import com.gresk.modules.review.domain.exception.ReviewForbiddenException;
import com.gresk.modules.review.domain.exception.ReviewNotFoundException;
import com.gresk.modules.review.domain.model.DetailedRating;
import com.gresk.modules.review.domain.model.Review;
import com.gresk.modules.review.domain.model.ReviewComment;
import com.gresk.modules.review.domain.port.out.ArtistRatingPort;
import com.gresk.modules.review.domain.port.out.EventRatingPort;
import com.gresk.modules.review.domain.port.out.ReviewRepository;
import com.gresk.modules.review.domain.port.out.UserPointsPort;
import com.gresk.modules.ticket.domain.model.Ticket;
import com.gresk.modules.ticket.domain.model.TicketId;
import com.gresk.modules.ticket.domain.port.out.TicketRepository;
import com.gresk.modules.user.domain.model.UserId;
import com.gresk.shared.domain.valueobject.ImageUrl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SubmitReviewUseCase implements SubmitReviewPort {

    private final ReviewRepository  reviewRepository;
    private final TicketRepository  ticketRepository;
    private final UserPointsPort    userPointsPort;
    private final EventRatingPort   eventRatingPort;
    private final ArtistRatingPort  artistRatingPort;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Review execute(SubmitReviewCommand command) {
        UserId   userId   = UserId.from(command.userId());
        TicketId ticketId = TicketId.from(command.ticketId());
        EventId  eventId  = EventId.of(command.eventId());

        // 1. Validate ticket exists and belongs to this user and event
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new ReviewNotFoundException(
                        "Ticket not found: " + command.ticketId()));

        if (!ticket.getUserId().equals(userId)) {
            throw new ReviewForbiddenException("Ticket does not belong to this user");
        }

        if (!ticket.getEventId().equals(eventId)) {
            throw new ReviewForbiddenException(
                    "Ticket does not correspond to the given event");
        }

        // 2. Validate no review exists yet for this ticket
        if (reviewRepository.existsByTicketId(ticketId)) {
            throw new ReviewAlreadyExistsException(
                    "A review already exists for ticket: " + command.ticketId());
        }

        // 3. Build value objects
        DetailedRating detailedRating = DetailedRating.of(
                command.artistRating(),  command.soundRating(),
                command.ambienceRating(), command.venueRating(),
                command.setlistRating()
        );
        ReviewComment comment  = ReviewComment.of(command.comment());
        ImageUrl      photoUrl = (command.photoUrl() != null && !command.photoUrl().isBlank())
                ? ImageUrl.of(command.photoUrl()) : null;

        // 4. Create and persist the review
        Review review = Review.create(userId, eventId, ticketId,
                detailedRating, comment, photoUrl);
        reviewRepository.save(review);

        // 5. Side effects
        userPointsPort.addPoints(userId, review.getPointsAwarded());

        eventRatingPort.addRating(
                eventId,
                detailedRating.artistRating().value(),
                detailedRating.soundRating().value(),
                detailedRating.ambienceRating().value(),
                detailedRating.venueRating().value(),
                detailedRating.setlistRating().value(),
                review.getOverallRating().value()
        );

        // Recalcular avgRating en el Artist vinculado (sólo artist_rating)
        artistRatingPort.recalculateForEvent(eventId);

        return review;
    }
}

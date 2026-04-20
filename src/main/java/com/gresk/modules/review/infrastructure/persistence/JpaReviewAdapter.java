package com.gresk.modules.review.infrastructure.persistence;

import com.gresk.modules.event.domain.model.EventId;
import com.gresk.modules.review.domain.model.Review;
import com.gresk.modules.review.domain.model.ReviewId;
import com.gresk.modules.review.domain.port.out.ReviewRepository;
import com.gresk.modules.ticket.domain.model.TicketId;
import com.gresk.modules.user.domain.model.UserId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class JpaReviewAdapter implements ReviewRepository {

    private final SpringDataReviewRepository repo;
    private final ReviewMapper               mapper;

    @Override
    @Transactional
    public Review save(Review review) {
        return mapper.toDomain(repo.save(mapper.toEntity(review)));
    }

    @Override
    public Optional<Review> findById(ReviewId id) {
        return repo.findById(id.value()).map(mapper::toDomain);
    }

    @Override
    public Optional<Review> findByTicketId(TicketId ticketId) {
        return repo.findByTicketId(ticketId.value()).map(mapper::toDomain);
    }

    @Override
    public List<Review> findByUserId(UserId userId) {
        return repo.findByUserId(userId.value()).stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public List<Review> findByEventId(EventId eventId) {
        return repo.findByEventId(eventId.value()).stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public boolean existsByTicketId(TicketId ticketId) {
        return repo.existsByTicketId(ticketId.value());
    }
}

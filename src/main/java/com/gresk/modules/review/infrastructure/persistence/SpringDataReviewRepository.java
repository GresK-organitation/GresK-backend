package com.gresk.modules.review.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SpringDataReviewRepository extends JpaRepository<ReviewEntity, UUID> {
    List<ReviewEntity>    findByUserId(UUID userId);
    List<ReviewEntity>    findByEventId(UUID eventId);
    Optional<ReviewEntity> findByTicketId(UUID ticketId);
    boolean               existsByTicketId(UUID ticketId);
}

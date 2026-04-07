package com.gresk.modules.ticket.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface SpringDataTicketRepository extends JpaRepository<JpaTicketEntity, UUID> {

    List<JpaTicketEntity> findByUserId(UUID userId);

    boolean existsByUserIdAndEventId(UUID userId, UUID eventId);
}

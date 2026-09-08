package com.gresk.modules.quotation.infrastructure.persistence.repository;

import com.gresk.modules.quotation.infrastructure.persistence.entity.EventQuoteEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface EventQuoteJpaRepository extends JpaRepository<EventQuoteEntity, UUID> {

    Optional<EventQuoteEntity> findByEventId(UUID eventId);
}

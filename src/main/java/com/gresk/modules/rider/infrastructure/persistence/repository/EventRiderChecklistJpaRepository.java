package com.gresk.modules.rider.infrastructure.persistence.repository;

import com.gresk.modules.rider.infrastructure.persistence.entity.EventRiderChecklistEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface EventRiderChecklistJpaRepository extends JpaRepository<EventRiderChecklistEntity, UUID> {

    Optional<EventRiderChecklistEntity> findByEventId(UUID eventId);

    @Query(value = """
            SELECT erc.* FROM event_rider_checklists erc
            JOIN events e ON e.id = erc.event_id
            WHERE e.status IN ('PUBLISHED', 'SOLD_OUT')
              AND e.event_date BETWEEN :from AND :to
              AND erc.alert_sent_at IS NULL
            """, nativeQuery = true)
    List<EventRiderChecklistEntity> findChecklistsNeedingAlert(
            @Param("from") Instant from,
            @Param("to") Instant to);

    @Query(value = """
            SELECT erc.* FROM event_rider_checklists erc
            JOIN events e ON e.id = erc.event_id
            WHERE e.promoter_id = :promoterId
              AND e.status IN ('PUBLISHED', 'SOLD_OUT')
              AND e.event_date BETWEEN :from AND :to
            ORDER BY e.event_date ASC
            """, nativeQuery = true)
    List<EventRiderChecklistEntity> findByPromoterIdAndDateRange(
            @Param("promoterId") UUID promoterId,
            @Param("from") Instant from,
            @Param("to") Instant to);
}

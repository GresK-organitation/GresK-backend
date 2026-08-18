package com.gresk.modules.tendencias.stats.infrastructure.persistence;

import com.gresk.modules.event.infrastructure.persistence.EventEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public interface TendenciasEventQueryRepository extends Repository<EventEntity, UUID> {

    @Query(value = """
        SELECT r.event_id AS eventId, e.title AS eventTitle, COUNT(r.id) AS reviewCount
        FROM reviews r
        JOIN events e ON e.id = r.event_id
        WHERE r.created_at BETWEEN :from AND :to
        GROUP BY r.event_id, e.title
        ORDER BY reviewCount DESC
        LIMIT :limit
        """, nativeQuery = true)
    List<MostDiscussedEventRow> findMostDiscussedEvents(
            @Param("from") Instant from, @Param("to") Instant to, @Param("limit") int limit);

    @Query(value = """
        SELECT id AS eventId, title AS eventTitle,
               CASE WHEN total_capacity > 0
                    THEN CAST(total_capacity - available_capacity AS DECIMAL(12,4)) / total_capacity * 100
                    ELSE 0.0
               END AS sellThroughPercent
        FROM events
        WHERE status IN ('PUBLISHED', 'FINISHED')
          AND event_date BETWEEN :from AND :to
          AND total_capacity IS NOT NULL AND total_capacity > 0
        ORDER BY sellThroughPercent DESC
        LIMIT :limit
        """, nativeQuery = true)
    List<HighestSellThroughRow> findHighestSellThrough(
            @Param("from") Instant from, @Param("to") Instant to, @Param("limit") int limit);
}

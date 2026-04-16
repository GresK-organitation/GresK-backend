package com.gresk.modules.promoter.infrastructure.persitence;

import com.gresk.modules.event.infrastructure.persistence.EventEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

/**
 * Read-only repository in the Promoter module.
 * Uses a native SQL LEFT JOIN to fetch event data together with real ticket
 * sales metrics in a single query (avoids N+1).
 */
public interface PromoterEventQueryRepository extends Repository<EventEntity, UUID> {

    @Query(value = """
            SELECT e.id,
                   e.title,
                   e.event_date,
                   e.venue,
                   e.city,
                   e.status::text          AS status,
                   e.total_capacity,
                   e.amount,
                   e.discounted_amount,
                   e.genre::text           AS genre,
                   e.cover_image_url,
                   COUNT(t.id)             AS tickets_sold
            FROM   events e
            LEFT JOIN tickets t
                   ON t.event_id = e.id
                  AND t.status IN ('PURCHASED', 'USED')
            WHERE  e.promoter_id = :promoterId
            GROUP  BY e.id
            ORDER  BY e.event_date DESC
            """,
            nativeQuery = true)
    List<PromoterEventSummary> findAllWithMetricsByPromoterId(
            @Param("promoterId") UUID promoterId
    );
}

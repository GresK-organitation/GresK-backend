package com.gresk.modules.tendencias.stats.infrastructure.persistence;

import com.gresk.modules.event.infrastructure.persistence.EventEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * "Sala más visitada": agrupa por el texto libre `events.venue` ya existente
 * (normalizado), sin necesitar un agregado Venue dedicado. Se retomará con
 * un módulo Venue completo cuando se aborde el mapa de calor por barrio.
 */
public interface TendenciasVenueQueryRepository extends Repository<EventEntity, UUID> {

    @Query(value = """
        SELECT MIN(e.venue) AS venueName,
               COUNT(DISTINCT t.id) AS attendeeCount,
               COUNT(DISTINCT e.id) AS eventCount
        FROM events e
        JOIN tickets t ON t.event_id = e.id AND t.status = 'PURCHASED'
        WHERE e.venue IS NOT NULL AND TRIM(e.venue) <> ''
          AND e.status IN ('PUBLISHED', 'FINISHED')
          AND e.event_date BETWEEN :from AND :to
        GROUP BY LOWER(TRIM(e.venue))
        ORDER BY attendeeCount DESC
        LIMIT :limit
        """, nativeQuery = true)
    List<MostVisitedVenueRow> findMostVisitedVenues(
            @Param("from") Instant from, @Param("to") Instant to, @Param("limit") int limit);
}

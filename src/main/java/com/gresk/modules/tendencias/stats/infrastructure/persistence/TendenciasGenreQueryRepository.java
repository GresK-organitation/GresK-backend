package com.gresk.modules.tendencias.stats.infrastructure.persistence;

import com.gresk.modules.event.infrastructure.persistence.EventEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public interface TendenciasGenreQueryRepository extends Repository<EventEntity, UUID> {

    /** Cuenta de eventos por género en una ventana temporal; se llama dos veces (actual y previa) desde el use case. */
    @Query(value = """
        SELECT genre AS genre, COUNT(*) AS eventCount
        FROM events
        WHERE genre IS NOT NULL
          AND status IN ('PUBLISHED', 'FINISHED')
          AND event_date BETWEEN :from AND :to
        GROUP BY genre
        """, nativeQuery = true)
    List<GenreCountRow> countEventsByGenre(@Param("from") Instant from, @Param("to") Instant to);
}

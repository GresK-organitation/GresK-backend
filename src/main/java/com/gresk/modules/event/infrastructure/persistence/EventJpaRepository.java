package com.gresk.modules.event.infrastructure.persistence;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface EventJpaRepository
        extends JpaRepository<EventEntity, UUID>,
                JpaSpecificationExecutor<EventEntity> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT e FROM EventEntity e WHERE e.id = :id")
    Optional<EventEntity> findByIdWithLock(@Param("id") UUID id);

    /**
     * Eventos de última hora: publicados, con inicio en las próximas 48 horas y plazas disponibles.
     */
    @Query(value = """
            SELECT * FROM events
            WHERE  status             = 'PUBLISHED'
              AND  event_date         >= NOW()
              AND  event_date         < NOW() + INTERVAL '48 hours'
              AND  available_capacity > 0
            ORDER BY event_date ASC
            """, nativeQuery = true)
    List<EventEntity> findLastMinuteEvents();
}

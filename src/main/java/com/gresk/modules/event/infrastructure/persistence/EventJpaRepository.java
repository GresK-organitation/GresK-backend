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
     * Eventos de última hora: publicados, futuros, con descuento activo y plazas disponibles.
     * Máximo 6 resultados ordenados por fecha ascendente.
     */
    @Query(value = """
            SELECT * FROM events
            WHERE  status             = 'PUBLISHED'
              AND  event_date         >= NOW()
              AND  discounted_amount  IS NOT NULL
              AND  available_capacity > 0
            ORDER BY event_date ASC
            LIMIT 6
            """, nativeQuery = true)
    List<EventEntity> findLastMinuteEvents();
}

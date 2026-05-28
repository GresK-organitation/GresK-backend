package com.gresk.modules.event.infrastructure.persistence;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
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

    /**
     * Candidatos para el flash deal: eventos publicados con flash deal activo,
     * aún no aplicado, con entradas disponibles y dentro de su propia ventana temporal.
     * El umbral es por fila (flash_deal_hours_threshold), lo que requiere SQL nativo.
     */
    @Query(value = """
            SELECT * FROM events
            WHERE  status                    = 'PUBLISHED'
              AND  flash_deal_enabled        = true
              AND  flash_deal_applied        = false
              AND  available_capacity        > 0
              AND  event_date                > NOW()
              AND  event_date               <= NOW() + (flash_deal_hours_threshold * INTERVAL '1 hour')
            ORDER BY event_date ASC
            """, nativeQuery = true)
    List<EventEntity> findEligibleForFlashDeal();

    @Query("""
            SELECT e FROM EventEntity e
            WHERE e.promoterId = :promoterId
              AND e.eventDate >= :from
              AND e.eventDate < :to
            ORDER BY e.eventDate ASC
            """)
    List<EventEntity> findByPromoterAndDateRange(
            @Param("promoterId") UUID promoterId,
            @Param("from") Instant from,
            @Param("to") Instant to
    );
}

package com.gresk.modules.promoter.infrastructure.persitence;

import com.gresk.modules.event.infrastructure.persistence.EventEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Repositorio de solo lectura para calcular estadísticas agregadas
 * de los eventos de un promotor. Vive en el módulo Promoter para
 * evitar dependencias inversas hacia el módulo Event.
 */
public interface PromoterStatsQueryRepository extends Repository<EventEntity, UUID> {

    /**
     * Recaudación total: ticketsSold × precio efectivo
     * Solo se contabilizan eventos PUBLISHED y FINISHED.
     */
    @Query(
        value = """
            SELECT COALESCE(
              SUM((total_capacity - available_capacity) * COALESCE(discounted_amount, amount)),
              0
            )
            FROM events
            WHERE promoter_id = :promoterId
              AND status IN ('PUBLISHED', 'FINISHED')
            """,
        nativeQuery = true
    )
    BigDecimal getTotalRevenue(@Param("promoterId") UUID promoterId);

    /**
     * Total de eventos publicados o finalizados del promotor.
     */
    @Query(
        value = """
            SELECT COUNT(*)
            FROM events
            WHERE promoter_id = :promoterId
              AND status IN ('PUBLISHED', 'FINISHED')
            """,
        nativeQuery = true
    )
    Long getTotalEvents(@Param("promoterId") UUID promoterId);

    /**
     * Asistentes totales: suma de tickets vendidos en eventos FINISHED.
     */
    @Query(
        value = """
            SELECT COALESCE(SUM(total_capacity - available_capacity), 0)
            FROM events
            WHERE promoter_id = :promoterId
              AND status = 'FINISHED'
            """,
        nativeQuery = true
    )
    Long getTotalAttendees(@Param("promoterId") UUID promoterId);

    /**
     * Sell-through: porcentaje de aforo vendido sobre eventos PUBLISHED y FINISHED.
     * Devuelve 0.0 si no hay aforo total.
     */
    @Query(
        value = """
            SELECT CASE
              WHEN SUM(total_capacity) > 0
              THEN CAST(SUM(total_capacity - available_capacity) AS DECIMAL(12,4))
                   / SUM(total_capacity) * 100
              ELSE 0.0
            END
            FROM events
            WHERE promoter_id = :promoterId
              AND status IN ('PUBLISHED', 'FINISHED')
            """,
        nativeQuery = true
    )
    Double getSellThrough(@Param("promoterId") UUID promoterId);

    /**
     * Eventos activos: PUBLISHED con fecha de evento futura.
     */
    @Query(
        value = """
            SELECT COUNT(*)
            FROM events
            WHERE promoter_id = :promoterId
              AND status = 'PUBLISHED'
              AND event_date > NOW()
            """,
        nativeQuery = true
    )
    Long getActiveEvents(@Param("promoterId") UUID promoterId);

    /**
     * Eventos pendientes: en estado DRAFT.
     */
    @Query(
        value = """
            SELECT COUNT(*)
            FROM events
            WHERE promoter_id = :promoterId
              AND status = 'DRAFT'
            """,
        nativeQuery = true
    )
    Long getPendingEvents(@Param("promoterId") UUID promoterId);

    /**
     * Precio medio de los eventos publicados o finalizados del promotor.
     */
    @Query(
        value = """
            SELECT COALESCE(AVG(COALESCE(discounted_amount, amount)), 0)
            FROM events
            WHERE promoter_id = :promoterId
              AND status IN ('PUBLISHED', 'FINISHED')
            """,
        nativeQuery = true
    )
    BigDecimal getAvgTicketPrice(@Param("promoterId") UUID promoterId);
}

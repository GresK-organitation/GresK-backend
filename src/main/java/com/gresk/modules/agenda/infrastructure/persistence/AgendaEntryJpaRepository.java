package com.gresk.modules.agenda.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public interface AgendaEntryJpaRepository extends JpaRepository<AgendaEntryEntity, UUID> {

    /** Entradas simples (sin recurrencia, no son excepciones) en el rango de fechas. */
    @Query("""
            SELECT e FROM AgendaEntryEntity e
            WHERE e.promoterId = :promoterId
              AND e.seriesId IS NULL
              AND e.recurrenceFrequency IS NULL
              AND e.startAt >= :from
              AND e.startAt <= :to
            ORDER BY e.startAt ASC
            """)
    List<AgendaEntryEntity> findSimpleByPromoterAndDateRange(
            @Param("promoterId") UUID promoterId,
            @Param("from") Instant from,
            @Param("to") Instant to
    );

    /**
     * Entradas maestras recurrentes (series_id IS NULL, frequency IS NOT NULL)
     * cuya serie puede tener ocurrencias en [from, to].
     */
    @Query("""
            SELECT e FROM AgendaEntryEntity e
            WHERE e.promoterId = :promoterId
              AND e.seriesId IS NULL
              AND e.recurrenceFrequency IS NOT NULL
              AND e.startAt <= :to
              AND (e.recurrenceUntil IS NULL OR e.recurrenceUntil >= :from)
            ORDER BY e.startAt ASC
            """)
    List<AgendaEntryEntity> findMastersWithPotentialOccurrences(
            @Param("promoterId") UUID promoterId,
            @Param("from") Instant from,
            @Param("to") Instant to
    );

    /** Todas las excepciones de una serie. */
    List<AgendaEntryEntity> findBySeriesId(UUID seriesId);

    /** Excepciones de una serie a partir de una fecha (inclusive). */
    @Query("""
            SELECT e FROM AgendaEntryEntity e
            WHERE e.seriesId = :seriesId
              AND e.exceptionDate >= :fromDate
            """)
    List<AgendaEntryEntity> findExceptionsBySeriesIdFromDate(
            @Param("seriesId") UUID seriesId,
            @Param("fromDate") Instant fromDate
    );

    /** Elimina excepciones de la serie a partir de una fecha (inclusive). */
    @Modifying
    @Query("""
            DELETE FROM AgendaEntryEntity e
            WHERE e.seriesId = :seriesId
              AND e.exceptionDate >= :fromDate
            """)
    void deleteExceptionsBySeriesIdFromDate(
            @Param("seriesId") UUID seriesId,
            @Param("fromDate") Instant fromDate
    );

    /** Elimina todas las excepciones de una serie. */
    @Modifying
    @Query("DELETE FROM AgendaEntryEntity e WHERE e.seriesId = :seriesId")
    void deleteAllExceptionsBySeriesId(@Param("seriesId") UUID seriesId);

    /**
     * Entradas pendientes de enviar recordatorio:
     * reminder_minutes_before IS NOT NULL, reminder_sent = false
     * y start_at <= now + reminder_minutes_before minutos.
     */
    @Query(value = """
            SELECT * FROM agenda_entries
            WHERE reminder_minutes_before IS NOT NULL
              AND reminder_sent = false
              AND start_at <= CAST(:now AS timestamptz) + (reminder_minutes_before * INTERVAL '1 minute')
            """, nativeQuery = true)
    List<AgendaEntryEntity> findPendingReminders(@Param("now") Instant now);
}

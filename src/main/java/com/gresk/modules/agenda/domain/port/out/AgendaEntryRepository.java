package com.gresk.modules.agenda.domain.port.out;

import com.gresk.modules.agenda.domain.model.AgendaEntry;
import com.gresk.modules.agenda.domain.model.AgendaEntryId;
import com.gresk.modules.promoter.domain.model.valueobject.PromoterId;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface AgendaEntryRepository {

    AgendaEntry save(AgendaEntry entry);

    Optional<AgendaEntry> findById(AgendaEntryId id);

    /** Entradas simples (sin recurrencia) cuyo start_at cae dentro del rango. */
    List<AgendaEntry> findSimpleByPromoterAndDateRange(PromoterId promoterId, Instant from, Instant to);

    /**
     * Entradas maestras recurrentes (series_id IS NULL, recurrence_frequency IS NOT NULL)
     * cuya serie podría tener ocurrencias en [from, to].
     */
    List<AgendaEntry> findMastersWithPotentialOccurrences(PromoterId promoterId, Instant from, Instant to);

    /** Excepciones (sobreescrituras o cancelaciones) de una serie concreta. */
    List<AgendaEntry> findExceptionsBySeriesId(AgendaEntryId seriesId);

    /**
     * Excepciones de la serie con exception_date >= fromDate.
     * Usado en la operación THIS_AND_FOLLOWING para limpiar excepciones futuras.
     */
    List<AgendaEntry> findExceptionsBySeriesIdFromDate(AgendaEntryId seriesId, Instant fromDate);

    void deleteById(AgendaEntryId id);

    /** Elimina todas las excepciones de la serie cuya exception_date >= fromDate. */
    void deleteExceptionsBySeriesIdFromDate(AgendaEntryId seriesId, Instant fromDate);

    /** Elimina todas las excepciones de una serie (clean slate para edición ALL). */
    void deleteAllExceptionsBySeriesId(AgendaEntryId seriesId);

    /** Entradas pendientes de enviar recordatorio (para el scheduler). */
    List<AgendaEntry> findPendingReminders(Instant now);
}

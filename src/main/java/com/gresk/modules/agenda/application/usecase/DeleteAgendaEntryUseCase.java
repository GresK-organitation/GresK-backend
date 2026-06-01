package com.gresk.modules.agenda.application.usecase;

import com.gresk.modules.agenda.domain.exception.AgendaEntryNotFoundException;
import com.gresk.modules.agenda.domain.exception.ForbiddenAgendaOperationException;
import com.gresk.modules.agenda.domain.model.AgendaEntry;
import com.gresk.modules.agenda.domain.model.AgendaEntryId;
import com.gresk.modules.agenda.domain.model.UpdateScope;
import com.gresk.modules.agenda.domain.port.out.AgendaEntryRepository;
import com.gresk.modules.promoter.domain.model.valueobject.PromoterId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
@RequiredArgsConstructor
@Transactional
public class DeleteAgendaEntryUseCase {

    private final AgendaEntryRepository repository;

    public void execute(String entryId, String promoterId, UpdateScope scope, Instant occurrenceDate) {
        AgendaEntry entry = repository.findById(AgendaEntryId.of(entryId))
                .orElseThrow(() -> new AgendaEntryNotFoundException(entryId));

        if (!entry.getPromoterId().equals(PromoterId.of(promoterId))) {
            throw new ForbiddenAgendaOperationException("You do not own this agenda entry");
        }

        UpdateScope resolvedScope = scope != null ? scope : UpdateScope.ALL;

        switch (resolvedScope) {
            case ALL -> {
                // Borrar toda la serie (el CASCADE se encarga de las excepciones)
                repository.deleteById(entry.getId());
            }
            case THIS_ONLY -> {
                if (occurrenceDate == null) {
                    throw new IllegalArgumentException("occurrenceDate is required for THIS_ONLY scope");
                }
                // Crear excepción de cancelación
                AgendaEntry cancellation = AgendaEntry.create(
                        entry.getType(), entry.getTitle(), entry.getPromoterId(),
                        null, occurrenceDate, null, false, null, null, null, null, null
                );
                cancellation.attachToSeries(entry.getId(), occurrenceDate);
                cancellation.cancel();
                repository.save(cancellation);
            }
            case THIS_AND_FOLLOWING -> {
                if (occurrenceDate == null) {
                    throw new IllegalArgumentException("occurrenceDate is required for THIS_AND_FOLLOWING scope");
                }
                // Truncar la serie y borrar excepciones futuras
                entry.truncateSeriesUntil(occurrenceDate.minusSeconds(1));
                repository.save(entry);
                repository.deleteExceptionsBySeriesIdFromDate(entry.getId(), occurrenceDate);
            }
        }
    }
}

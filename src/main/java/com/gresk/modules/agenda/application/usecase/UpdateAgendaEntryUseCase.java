package com.gresk.modules.agenda.application.usecase;

import com.gresk.modules.agenda.application.command.UpdateAgendaEntryCommand;
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

import java.time.temporal.ChronoUnit;

@Service
@RequiredArgsConstructor
@Transactional
public class UpdateAgendaEntryUseCase {

    private final AgendaEntryRepository repository;

    public AgendaEntry execute(UpdateAgendaEntryCommand cmd) {
        AgendaEntry entry = repository.findById(AgendaEntryId.of(cmd.entryId()))
                .orElseThrow(() -> new AgendaEntryNotFoundException(cmd.entryId()));

        assertOwnership(entry, cmd.promoterId());

        UpdateScope scope = cmd.scope() != null ? cmd.scope() : UpdateScope.ALL;

        return switch (scope) {
            case ALL              -> updateAll(entry, cmd);
            case THIS_ONLY        -> updateThisOnly(entry, cmd);
            case THIS_AND_FOLLOWING -> updateThisAndFollowing(entry, cmd);
        };
    }

    // ── Scope: ALL ───────────────────────────────────────────────────────────

    private AgendaEntry updateAll(AgendaEntry entry, UpdateAgendaEntryCommand cmd) {
        entry.update(cmd.title(), cmd.description(), cmd.startAt(), cmd.endAt(),
                cmd.allDay(), cmd.color(), cmd.label(), cmd.linkedEntity(),
                cmd.reminderMinutesBefore());

        if (entry.isRecurring()) {
            // Borrar todas las excepciones existentes (clean slate)
            repository.deleteAllExceptionsBySeriesId(entry.getId());
        }
        return repository.save(entry);
    }

    // ── Scope: THIS_ONLY ─────────────────────────────────────────────────────

    private AgendaEntry updateThisOnly(AgendaEntry master, UpdateAgendaEntryCommand cmd) {
        if (cmd.occurrenceDate() == null) {
            throw new IllegalArgumentException("occurrenceDate is required for THIS_ONLY scope");
        }
        // Crear una entrada excepción que sobreescribe esta ocurrencia
        AgendaEntry exception = AgendaEntry.create(
                master.getType(),
                cmd.title(),
                master.getPromoterId(),
                cmd.description(),
                cmd.startAt(),
                cmd.endAt(),
                cmd.allDay(),
                cmd.color(),
                cmd.label(),
                cmd.linkedEntity(),
                null,   // las excepciones no tienen recurrencia propia
                cmd.reminderMinutesBefore()
        );
        exception.attachToSeries(master.getId(), cmd.occurrenceDate());
        return repository.save(exception);
    }

    // ── Scope: THIS_AND_FOLLOWING ────────────────────────────────────────────

    private AgendaEntry updateThisAndFollowing(AgendaEntry master, UpdateAgendaEntryCommand cmd) {
        if (cmd.occurrenceDate() == null) {
            throw new IllegalArgumentException("occurrenceDate is required for THIS_AND_FOLLOWING scope");
        }
        // 1. Truncar la serie original hasta el día anterior
        master.truncateSeriesUntil(cmd.occurrenceDate().minus(1, ChronoUnit.SECONDS));
        repository.save(master);

        // 2. Borrar excepciones de la serie original a partir de esta fecha
        repository.deleteExceptionsBySeriesIdFromDate(master.getId(), cmd.occurrenceDate());

        // 3. Crear nueva serie maestra desde la fecha indicada
        AgendaEntry newMaster = AgendaEntry.create(
                master.getType(),
                cmd.title(),
                master.getPromoterId(),
                cmd.description(),
                cmd.startAt(),
                cmd.endAt(),
                cmd.allDay(),
                cmd.color(),
                cmd.label(),
                cmd.linkedEntity(),
                master.getRecurrenceRule(),  // hereda la regla de recurrencia
                cmd.reminderMinutesBefore()
        );
        return repository.save(newMaster);
    }

    private void assertOwnership(AgendaEntry entry, String promoterId) {
        if (!entry.getPromoterId().equals(PromoterId.of(promoterId))) {
            throw new ForbiddenAgendaOperationException("You do not own this agenda entry");
        }
    }
}

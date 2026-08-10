package com.gresk.modules.agenda.application.usecase;

import com.gresk.modules.agenda.application.command.CreateAgendaEntryCommand;
import com.gresk.modules.agenda.domain.model.AgendaEntry;
import com.gresk.modules.agenda.domain.port.out.AgendaEntryRepository;
import com.gresk.modules.promoter.domain.model.valueobject.PromoterId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class CreateAgendaEntryUseCase {

    private final AgendaEntryRepository repository;

    public AgendaEntry execute(CreateAgendaEntryCommand cmd) {
        AgendaEntry entry = AgendaEntry.create(
                cmd.type(),
                cmd.title(),
                PromoterId.of(cmd.promoterId()),
                cmd.description(),
                cmd.startAt(),
                cmd.endAt(),
                cmd.allDay(),
                cmd.color(),
                cmd.label(),
                cmd.linkedEntity(),
                cmd.recurrenceRule(),
                cmd.reminderMinutesBefore()
        );
        return repository.save(entry);
    }
}

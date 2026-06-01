package com.gresk.modules.agenda.application.usecase;

import com.gresk.modules.agenda.domain.exception.AgendaEntryNotFoundException;
import com.gresk.modules.agenda.domain.exception.ForbiddenAgendaOperationException;
import com.gresk.modules.agenda.domain.model.AgendaEntry;
import com.gresk.modules.agenda.domain.model.AgendaEntryId;
import com.gresk.modules.agenda.domain.port.out.AgendaEntryRepository;
import com.gresk.modules.promoter.domain.model.valueobject.PromoterId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GetAgendaEntryUseCase {

    private final AgendaEntryRepository repository;

    public AgendaEntry execute(String entryId, String promoterId) {
        AgendaEntry entry = repository.findById(AgendaEntryId.of(entryId))
                .orElseThrow(() -> new AgendaEntryNotFoundException(entryId));

        if (!entry.getPromoterId().equals(PromoterId.of(promoterId))) {
            throw new ForbiddenAgendaOperationException("You do not own this agenda entry");
        }
        return entry;
    }
}

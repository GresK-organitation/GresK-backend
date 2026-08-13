package com.gresk.modules.journal.application.usecase;

import com.gresk.modules.artist.domain.model.valueobject.ArtistId;
import com.gresk.modules.event.domain.model.EventId;
import com.gresk.modules.journal.application.command.UpdateJournalEntryCommand;
import com.gresk.modules.journal.application.port.in.UpdateJournalEntryPort;
import com.gresk.modules.journal.domain.exception.JournalEntryForbiddenException;
import com.gresk.modules.journal.domain.exception.JournalEntryNotFoundException;
import com.gresk.modules.journal.domain.model.ApproxDate;
import com.gresk.modules.journal.domain.model.JournalEntry;
import com.gresk.modules.journal.domain.model.JournalEntryId;
import com.gresk.modules.journal.domain.model.RatingCriterion;
import com.gresk.modules.journal.domain.port.out.JournalEntryRepository;
import com.gresk.modules.user.domain.model.UserId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UpdateJournalEntryUseCase implements UpdateJournalEntryPort {

    private final JournalEntryRepository repository;
    private final CatalogLinkResolver    catalogLinkResolver;

    @Override
    @Transactional
    public JournalEntry execute(UpdateJournalEntryCommand command) {
        JournalEntryId id     = JournalEntryId.of(command.entryId());
        UserId         userId = UserId.from(command.userId());

        JournalEntry entry = repository.findById(id)
                .orElseThrow(() -> new JournalEntryNotFoundException("Journal entry not found: " + command.entryId()));

        if (!entry.getUserId().equals(userId)) {
            throw new JournalEntryForbiddenException("Journal entry does not belong to this user");
        }

        ArtistId artistId = catalogLinkResolver.resolveArtistId(command.artistId());
        EventId  eventId  = catalogLinkResolver.resolveEventId(command.eventId());
        ApproxDate date = ApproxDate.reconstitute(command.date(),
                CreateJournalEntryUseCase.parsePrecision(command.datePrecision()));
        List<RatingCriterion> criteria = CreateJournalEntryUseCase.toCriteria(command.criteria());

        entry.update(command.artistName(), artistId, date, command.venueName(), command.city(),
                eventId, command.notes(), criteria, command.genre());

        return repository.save(entry);
    }
}

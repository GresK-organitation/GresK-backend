package com.gresk.modules.journal.application.usecase;

import com.gresk.modules.artist.domain.model.valueobject.ArtistId;
import com.gresk.modules.event.domain.model.EventId;
import com.gresk.modules.journal.application.command.CreateJournalEntryCommand;
import com.gresk.modules.journal.application.command.RatingCriterionInput;
import com.gresk.modules.journal.application.event.JournalEntryCreatedEvent;
import com.gresk.modules.journal.application.port.in.CreateJournalEntryPort;
import com.gresk.modules.journal.domain.exception.InvalidJournalEntryException;
import com.gresk.modules.journal.domain.model.ApproxDate;
import com.gresk.modules.journal.domain.model.DatePrecision;
import com.gresk.modules.journal.domain.model.JournalEntry;
import com.gresk.modules.journal.domain.model.RatingCriterion;
import com.gresk.modules.journal.domain.port.out.JournalEntryRepository;
import com.gresk.modules.user.domain.model.UserId;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CreateJournalEntryUseCase implements CreateJournalEntryPort {

    private final JournalEntryRepository    repository;
    private final CatalogLinkResolver       catalogLinkResolver;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    @Transactional
    public JournalEntry execute(CreateJournalEntryCommand command) {
        UserId   userId   = UserId.from(command.userId());
        ArtistId artistId = catalogLinkResolver.resolveArtistId(command.artistId());
        EventId  eventId  = catalogLinkResolver.resolveEventId(command.eventId());

        ApproxDate date = ApproxDate.reconstitute(command.date(), parsePrecision(command.datePrecision()));
        List<RatingCriterion> criteria = toCriteria(command.criteria());

        JournalEntry entry = JournalEntry.create(
                userId, command.artistName(), artistId, date,
                command.venueName(), command.city(), eventId,
                command.notes(), criteria, command.genre(),
                command.visibility(), command.source()
        );

        JournalEntry saved = repository.save(entry);
        eventPublisher.publishEvent(new JournalEntryCreatedEvent(userId.value()));
        return saved;
    }

    static DatePrecision parsePrecision(String value) {
        try {
            return DatePrecision.valueOf(value);
        } catch (IllegalArgumentException | NullPointerException e) {
            throw new InvalidJournalEntryException("Invalid date precision: " + value);
        }
    }

    static List<RatingCriterion> toCriteria(List<RatingCriterionInput> inputs) {
        if (inputs == null) return List.of();
        return inputs.stream()
                .map(i -> RatingCriterion.of(i.label(), i.value()))
                .toList();
    }
}

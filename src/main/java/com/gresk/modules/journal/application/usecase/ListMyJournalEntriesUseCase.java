package com.gresk.modules.journal.application.usecase;

import com.gresk.modules.artist.domain.model.valueobject.ArtistId;
import com.gresk.modules.journal.application.query.ListMyJournalEntriesQuery;
import com.gresk.modules.journal.domain.model.JournalEntry;
import com.gresk.modules.journal.domain.port.out.JournalEntryFilter;
import com.gresk.modules.journal.domain.port.out.JournalEntryRepository;
import com.gresk.modules.user.domain.model.UserId;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ListMyJournalEntriesUseCase {

    private final JournalEntryRepository repository;

    @Transactional(readOnly = true)
    public List<JournalEntry> execute(ListMyJournalEntriesQuery query) {
        return repository.findAll(toFilter(query), PageRequest.of(query.page(), query.size()));
    }

    @Transactional(readOnly = true)
    public long count(ListMyJournalEntriesQuery query) {
        return repository.count(toFilter(query));
    }

    private JournalEntryFilter toFilter(ListMyJournalEntriesQuery query) {
        return new JournalEntryFilter(
                UserId.from(query.userId()),
                Optional.ofNullable(query.artistId()).map(ArtistId::of),
                Optional.ofNullable(query.genre()),
                Optional.ofNullable(query.dateFrom()),
                Optional.ofNullable(query.dateTo())
        );
    }
}

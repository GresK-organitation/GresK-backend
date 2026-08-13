package com.gresk.modules.journal.domain.port.out;

import com.gresk.modules.journal.domain.model.JournalEntry;
import com.gresk.modules.journal.domain.model.JournalEntryId;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.Optional;

public interface JournalEntryRepository {
    JournalEntry save(JournalEntry entry);
    Optional<JournalEntry> findById(JournalEntryId id);
    List<JournalEntry> findAll(JournalEntryFilter filter, PageRequest pageRequest);
    long count(JournalEntryFilter filter);
    void deleteById(JournalEntryId id);
}

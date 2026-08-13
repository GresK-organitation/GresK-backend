package com.gresk.modules.curation.infrastructure.adapter;

import com.gresk.modules.curation.domain.port.out.JournalEntryLookupPort;
import com.gresk.modules.journal.domain.model.JournalEntryId;
import com.gresk.modules.journal.domain.port.out.JournalEntryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class JournalEntryLookupAdapter implements JournalEntryLookupPort {

    private final JournalEntryRepository journalEntryRepository;

    @Override
    public boolean existsById(UUID entryId) {
        return journalEntryRepository.findById(JournalEntryId.of(entryId)).isPresent();
    }
}

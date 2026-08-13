package com.gresk.modules.journal.application.usecase;

import com.gresk.modules.journal.domain.exception.JournalEntryForbiddenException;
import com.gresk.modules.journal.domain.exception.JournalEntryNotFoundException;
import com.gresk.modules.journal.domain.model.JournalEntry;
import com.gresk.modules.journal.domain.model.JournalEntryId;
import com.gresk.modules.journal.domain.model.JournalVisibility;
import com.gresk.modules.journal.domain.port.out.JournalEntryRepository;
import com.gresk.modules.user.domain.model.UserId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class GetJournalEntryUseCase {

    private final JournalEntryRepository repository;

    @Transactional(readOnly = true)
    public JournalEntry execute(String entryId, String requesterUserId) {
        JournalEntry entry = repository.findById(JournalEntryId.of(entryId))
                .orElseThrow(() -> new JournalEntryNotFoundException("Journal entry not found: " + entryId));

        boolean isOwner = entry.getUserId().equals(UserId.from(requesterUserId));
        if (!isOwner && entry.getVisibility() != JournalVisibility.PUBLIC) {
            throw new JournalEntryForbiddenException("Journal entry is private");
        }

        return entry;
    }
}

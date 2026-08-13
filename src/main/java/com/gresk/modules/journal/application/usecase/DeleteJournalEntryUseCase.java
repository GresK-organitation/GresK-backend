package com.gresk.modules.journal.application.usecase;

import com.gresk.modules.journal.application.port.in.DeleteJournalEntryPort;
import com.gresk.modules.journal.domain.exception.JournalEntryForbiddenException;
import com.gresk.modules.journal.domain.exception.JournalEntryNotFoundException;
import com.gresk.modules.journal.domain.model.JournalEntry;
import com.gresk.modules.journal.domain.model.JournalEntryId;
import com.gresk.modules.journal.domain.port.out.JournalEntryRepository;
import com.gresk.modules.user.domain.model.UserId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DeleteJournalEntryUseCase implements DeleteJournalEntryPort {

    private final JournalEntryRepository repository;

    @Override
    @Transactional
    public void execute(String entryId, String userId) {
        JournalEntryId id = JournalEntryId.of(entryId);
        JournalEntry entry = repository.findById(id)
                .orElseThrow(() -> new JournalEntryNotFoundException("Journal entry not found: " + entryId));

        if (!entry.getUserId().equals(UserId.from(userId))) {
            throw new JournalEntryForbiddenException("Journal entry does not belong to this user");
        }

        repository.deleteById(id);
    }
}

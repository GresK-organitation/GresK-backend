package com.gresk.modules.journal.application.usecase;

import com.gresk.modules.journal.application.command.CreateJournalEntryCommand;
import com.gresk.modules.journal.application.dto.BulkCreateFailure;
import com.gresk.modules.journal.application.dto.BulkCreateResult;
import com.gresk.modules.journal.application.port.in.CreateJournalEntryPort;
import com.gresk.modules.journal.domain.model.JournalEntry;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Reuses {@link CreateJournalEntryPort} per item, each in its own transaction
 * (this class is deliberately NOT @Transactional) so one invalid entry in a
 * retroactive import batch doesn't roll back the ones that succeeded.
 */
@Service
@RequiredArgsConstructor
public class BulkCreateJournalEntriesUseCase {

    private final CreateJournalEntryPort createJournalEntryPort;

    public BulkCreateResult execute(List<CreateJournalEntryCommand> commands) {
        List<JournalEntry>    created  = new ArrayList<>();
        List<BulkCreateFailure> failures = new ArrayList<>();

        for (int i = 0; i < commands.size(); i++) {
            try {
                created.add(createJournalEntryPort.execute(commands.get(i)));
            } catch (RuntimeException e) {
                failures.add(new BulkCreateFailure(i, e.getMessage()));
            }
        }

        return new BulkCreateResult(created, failures);
    }
}

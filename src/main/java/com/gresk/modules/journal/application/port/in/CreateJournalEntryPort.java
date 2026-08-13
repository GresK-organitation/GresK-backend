package com.gresk.modules.journal.application.port.in;

import com.gresk.modules.journal.application.command.CreateJournalEntryCommand;
import com.gresk.modules.journal.domain.model.JournalEntry;

public interface CreateJournalEntryPort {
    JournalEntry execute(CreateJournalEntryCommand command);
}

package com.gresk.modules.journal.application.port.in;

import com.gresk.modules.journal.application.command.UpdateJournalEntryCommand;
import com.gresk.modules.journal.domain.model.JournalEntry;

public interface UpdateJournalEntryPort {
    JournalEntry execute(UpdateJournalEntryCommand command);
}

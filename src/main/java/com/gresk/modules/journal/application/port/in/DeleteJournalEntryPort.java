package com.gresk.modules.journal.application.port.in;

public interface DeleteJournalEntryPort {
    void execute(String entryId, String userId);
}

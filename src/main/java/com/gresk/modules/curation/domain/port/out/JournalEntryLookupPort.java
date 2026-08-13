package com.gresk.modules.curation.domain.port.out;

import java.util.UUID;

/**
 * Read-only existence check into the journal module — used only to validate
 * a JOURNAL_ENTRY item before it's added to a list.
 */
public interface JournalEntryLookupPort {
    boolean existsById(UUID entryId);
}

package com.gresk.modules.journal.infrastructure.web.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

import java.util.List;

public record BulkCreateJournalEntriesRequest(
        @NotEmpty(message = "entries must not be empty")
        @Size(max = 100, message = "at most 100 entries can be imported at once")
        @Valid
        List<CreateJournalEntryRequest> entries
) {}

package com.gresk.modules.journal.infrastructure.web;

import java.util.List;

public record BulkCreateResultResponse(
        List<JournalEntryResponse> created,
        List<BulkCreateFailureResponse> failures
) {
    public record BulkCreateFailureResponse(int index, String reason) {}
}

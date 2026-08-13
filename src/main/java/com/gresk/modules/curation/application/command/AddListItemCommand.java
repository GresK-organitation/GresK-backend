package com.gresk.modules.curation.application.command;

public record AddListItemCommand(
        String listId,
        String userId,     // requester, for ownership check
        String entryType,  // VERIFIED_REVIEW | JOURNAL_ENTRY
        String entryId
) {}

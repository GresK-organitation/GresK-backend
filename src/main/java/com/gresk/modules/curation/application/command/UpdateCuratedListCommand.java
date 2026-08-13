package com.gresk.modules.curation.application.command;

public record UpdateCuratedListCommand(
        String listId,
        String userId,        // requester, for ownership check
        String title,
        String description,   // nullable
        String visibility     // PRIVATE | PUBLIC
) {}

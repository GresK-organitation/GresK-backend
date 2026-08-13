package com.gresk.modules.curation.application.command;

public record CreateCuratedListCommand(
        String ownerId,
        String title,
        String description,   // nullable
        String visibility     // PRIVATE | PUBLIC
) {}

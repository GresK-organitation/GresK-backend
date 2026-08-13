package com.gresk.modules.journal.infrastructure.web;

public record JournalMediaResponse(
        String  mediaId,
        String  mediaType,
        String  url,
        int     displayOrder,
        Integer durationSeconds
) {}

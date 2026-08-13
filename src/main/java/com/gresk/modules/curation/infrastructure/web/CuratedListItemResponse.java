package com.gresk.modules.curation.infrastructure.web;

public record CuratedListItemResponse(
        String itemId,
        String entryType,
        String entryId,
        int    position,
        String addedAt
) {}

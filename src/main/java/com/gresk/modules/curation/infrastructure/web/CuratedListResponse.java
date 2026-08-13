package com.gresk.modules.curation.infrastructure.web;

import java.util.List;

public record CuratedListResponse(
        String listId,
        String ownerId,
        String title,
        String description,
        String visibility,
        List<CuratedListItemResponse> items,
        String createdAt,
        String updatedAt
) {}

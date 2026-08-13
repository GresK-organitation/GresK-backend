package com.gresk.modules.curation.infrastructure.web.request;

import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record ReorderListItemsRequest(
        @NotEmpty(message = "itemIds must not be empty")
        List<String> itemIds
) {}

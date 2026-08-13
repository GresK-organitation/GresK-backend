package com.gresk.modules.curation.infrastructure.web.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record AddListItemRequest(
        @NotBlank(message = "entryType is required")
        @Pattern(regexp = "VERIFIED_REVIEW|JOURNAL_ENTRY", message = "entryType must be VERIFIED_REVIEW or JOURNAL_ENTRY")
        String entryType,

        @NotBlank(message = "entryId is required")
        String entryId
) {}

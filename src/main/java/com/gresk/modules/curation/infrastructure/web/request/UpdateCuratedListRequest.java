package com.gresk.modules.curation.infrastructure.web.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UpdateCuratedListRequest(
        @NotBlank(message = "title must not be blank")
        @Size(max = 120, message = "title must not exceed 120 characters")
        String title,

        @Size(max = 500, message = "description must not exceed 500 characters")
        String description,

        @NotNull(message = "visibility is required")
        @Pattern(regexp = "PRIVATE|PUBLIC", message = "visibility must be PRIVATE or PUBLIC")
        String visibility
) {}

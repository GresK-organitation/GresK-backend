package com.gresk.modules.tendencias.chronicle.infrastructure.web.request;

import jakarta.validation.constraints.NotBlank;

public record RegisterFeedSourceRequest(
        @NotBlank String name,
        @NotBlank String feedUrl,
        String sourceUrl
) {
}

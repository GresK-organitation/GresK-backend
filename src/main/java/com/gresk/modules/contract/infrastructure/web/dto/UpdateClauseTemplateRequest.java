package com.gresk.modules.contract.infrastructure.web.dto;

import jakarta.validation.constraints.NotBlank;

public record UpdateClauseTemplateRequest(
        @NotBlank String title,
        @NotBlank String contentTemplate
) {}

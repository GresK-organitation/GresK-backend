package com.gresk.modules.email.infrastructure.web.dto;

import jakarta.validation.constraints.NotBlank;

public record EditDraftRequest(@NotBlank String body) {}

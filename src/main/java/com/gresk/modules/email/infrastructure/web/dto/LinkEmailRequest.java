package com.gresk.modules.email.infrastructure.web.dto;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record LinkEmailRequest(@NotNull UUID eventId) {}

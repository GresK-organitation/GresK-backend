package com.gresk.modules.booking.infrastructure.web.dto;

import jakarta.validation.constraints.NotBlank;

import java.time.Instant;

public record PromoteHoldRequest(@NotBlank String targetStatus, Instant newHoldExpiresAt) {
}

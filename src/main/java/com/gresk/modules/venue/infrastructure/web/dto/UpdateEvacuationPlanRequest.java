package com.gresk.modules.venue.infrastructure.web.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record UpdateEvacuationPlanRequest(
        @NotBlank String documentAssetId,
        @Min(1)   int certifiedCapacity,
        @NotNull  LocalDate lastReviewedAt,
        String reviewedBy
) {}

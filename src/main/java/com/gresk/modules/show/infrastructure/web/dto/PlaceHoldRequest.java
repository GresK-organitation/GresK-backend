package com.gresk.modules.show.infrastructure.web.dto;

import jakarta.validation.constraints.Min;

public record PlaceHoldRequest(@Min(1) int holdDurationHours) {}

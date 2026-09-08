package com.gresk.modules.booking.infrastructure.web.dto;

import jakarta.validation.constraints.NotNull;

import java.time.Instant;

public record RescheduleBookingRequest(@NotNull Instant newEventDate, String reason) {
}

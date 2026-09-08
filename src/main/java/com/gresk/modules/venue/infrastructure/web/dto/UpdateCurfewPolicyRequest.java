package com.gresk.modules.venue.infrastructure.web.dto;

import jakarta.validation.constraints.NotNull;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.Set;

public record UpdateCurfewPolicyRequest(
        @NotNull LocalTime hardCutoff,
        Integer maxDecibels,
        Set<DayOfWeek> restrictedDays,
        String notes
) {}

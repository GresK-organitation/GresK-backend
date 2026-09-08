package com.gresk.modules.logistics.infrastructure.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record UpdateTourDetailsRequest(@NotBlank String name, @NotNull LocalDate startDate,
                                        @NotNull LocalDate endDate, String notes) {
}

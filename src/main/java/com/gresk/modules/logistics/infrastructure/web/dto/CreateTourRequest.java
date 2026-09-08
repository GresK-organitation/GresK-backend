package com.gresk.modules.logistics.infrastructure.web.dto;

import com.gresk.modules.logistics.application.command.TourLegInput;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.List;

public record CreateTourRequest(@NotBlank String artistId, @NotBlank String name, @NotNull LocalDate startDate,
                                 @NotNull LocalDate endDate, String notes, List<TourLegInput> legs) {
}

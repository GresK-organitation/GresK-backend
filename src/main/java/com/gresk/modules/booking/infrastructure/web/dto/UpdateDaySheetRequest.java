package com.gresk.modules.booking.infrastructure.web.dto;

import com.gresk.modules.booking.application.command.DaySheetEntryInput;
import jakarta.validation.constraints.NotBlank;

import java.util.List;

public record UpdateDaySheetRequest(@NotBlank String showDate, List<DaySheetEntryInput> entries) {
}

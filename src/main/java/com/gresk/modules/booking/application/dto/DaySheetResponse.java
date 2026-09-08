package com.gresk.modules.booking.application.dto;

import java.util.List;

public record DaySheetResponse(String showDate, List<DaySheetEntryResponse> entries) {
}

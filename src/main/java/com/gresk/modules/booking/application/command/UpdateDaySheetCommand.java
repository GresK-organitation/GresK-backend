package com.gresk.modules.booking.application.command;

import java.util.List;

public record UpdateDaySheetCommand(String bookingId, String promoterId, String showDate, List<DaySheetEntryInput> entries) {
}

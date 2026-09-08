package com.gresk.modules.booking.application.command;

public record DaySheetEntryInput(String time, String type, String label, String notes) {
}

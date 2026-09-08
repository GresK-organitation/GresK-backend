package com.gresk.modules.logistics.application.command;

import java.time.LocalDate;

public record TourLegInput(String bookingId, int sequenceOrder, LocalDate showDate, String venueName, String venueCity) {
}

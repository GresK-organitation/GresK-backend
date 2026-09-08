package com.gresk.modules.booking.application.dto;

import java.time.Instant;
import java.util.List;

public record BookingResponse(
        String id,
        String promoterId,
        String artistId,
        String venueId,
        String venueName,
        String venueCountry,
        String venueRegion,
        String venueCity,
        Double venueRadiusKm,
        Instant eventDate,
        String status,
        Instant holdExpiresAt,
        List<MilestoneResponse> milestones,
        DaySheetResponse daySheet,
        String exclusivityCountry,
        String exclusivityRegion,
        String exclusivityCity,
        Double exclusivityRadiusKm,
        Integer exclusivityDaysBefore,
        Integer exclusivityDaysAfter,
        String linkedEventId,
        String linkedContractId,
        String notes,
        Instant createdAt,
        Instant updatedAt
) {
}

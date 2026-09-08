package com.gresk.modules.logistics.application.dto;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

public record TourResponse(String id, String promoterId, String artistId, String name, LocalDate startDate,
                            LocalDate endDate, String status, List<LegView> legs, List<ContactView> emergencyContacts,
                            List<PoiView> pointsOfInterest, String notes, Instant createdAt, Instant updatedAt) {

    public record LegView(String bookingId, int sequenceOrder, LocalDate showDate, String venueName, String venueCity) {
    }

    public record ContactView(String name, String role, String phone, String notes) {
    }

    public record PoiView(String name, String category, String address, Double latitude, Double longitude, String notes) {
    }
}

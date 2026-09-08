package com.gresk.modules.booking.application.command;

public record VenueRefInput(String venueId, String venueName, String country, String region, String city, Double radiusKm) {
}

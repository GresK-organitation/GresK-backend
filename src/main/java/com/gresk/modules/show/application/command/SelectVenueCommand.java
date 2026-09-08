package com.gresk.modules.show.application.command;

public record SelectVenueCommand(String showId, String promoterId, String venueId, String capacityConfigCode) {
}

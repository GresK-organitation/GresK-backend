package com.gresk.modules.venue.application.command;

public record RegisterVenueCommand(
        String promoterId,
        String name,
        String street,
        String city,
        String country
) {
}

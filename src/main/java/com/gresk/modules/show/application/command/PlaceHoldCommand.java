package com.gresk.modules.show.application.command;

public record PlaceHoldCommand(String showId, String promoterId, int holdDurationHours) {
}

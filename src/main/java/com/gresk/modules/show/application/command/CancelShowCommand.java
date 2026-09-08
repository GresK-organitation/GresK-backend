package com.gresk.modules.show.application.command;

public record CancelShowCommand(String showId, String promoterId, String reason) {
}

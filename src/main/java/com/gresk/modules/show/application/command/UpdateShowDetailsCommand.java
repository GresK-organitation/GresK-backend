package com.gresk.modules.show.application.command;

import java.time.Instant;

public record UpdateShowDetailsCommand(String showId, String promoterId, String name, Instant scheduledDate) {
}

package com.gresk.modules.show.application.command;

import java.time.Instant;

public record CreateShowCommand(String promoterId, String name, Instant tentativeDate) {
}

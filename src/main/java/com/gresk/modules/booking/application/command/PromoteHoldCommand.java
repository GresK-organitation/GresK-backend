package com.gresk.modules.booking.application.command;

import java.time.Instant;

public record PromoteHoldCommand(String bookingId, String promoterId, String targetStatus, Instant newHoldExpiresAt) {
}

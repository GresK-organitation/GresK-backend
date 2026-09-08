package com.gresk.modules.booking.application.query;

import java.time.Instant;

public record ListBookingsQuery(String promoterId, Instant from, Instant to, String status) {
}

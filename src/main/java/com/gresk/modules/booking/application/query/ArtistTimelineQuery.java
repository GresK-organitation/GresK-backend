package com.gresk.modules.booking.application.query;

import java.time.Instant;

public record ArtistTimelineQuery(String promoterId, String artistId, Instant from, Instant to) {
}

package com.gresk.modules.event.domain.port.out;

import com.gresk.modules.event.domain.model.EventStatus;
import com.gresk.shared.domain.MusicGenre;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Optional;

public record EventFilter(
        Optional<MusicGenre>  genre,
        Optional<String>      city,
        Optional<Instant>     dateFrom,
        Optional<Instant>     dateTo,
        Optional<BigDecimal>  minPrice,
        Optional<BigDecimal>  maxPrice,
        Optional<String>      artistName,
        Optional<EventStatus> status
) {
    public static EventFilter empty() {
        return new EventFilter(
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                Optional.of(EventStatus.PUBLISHED)
        );
    }

    public static EventFilter publishedOnly() {
        return empty();
    }
}

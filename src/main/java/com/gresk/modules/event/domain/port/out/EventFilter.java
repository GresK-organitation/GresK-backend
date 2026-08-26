package com.gresk.modules.event.domain.port.out;

import com.gresk.modules.event.domain.model.EventStatus;
import com.gresk.shared.domain.MusicGenre;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

public record EventFilter(
        Optional<MusicGenre>  genre,
        Optional<String>      city,
        Optional<Instant>     dateFrom,
        Optional<Instant>     dateTo,
        Optional<BigDecimal>  minPrice,
        Optional<BigDecimal>  maxPrice,
        Optional<String>      artistName,
        Optional<EventStatus> status,
        Optional<UUID>        artistId
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
                Optional.of(EventStatus.PUBLISHED),
                Optional.empty()
        );
    }

    public static EventFilter forArtist(UUID artistId, Instant from) {
        return new EventFilter(
                Optional.empty(), Optional.empty(), Optional.of(from), Optional.empty(),
                Optional.empty(), Optional.empty(), Optional.empty(),
                Optional.of(EventStatus.PUBLISHED), Optional.of(artistId)
        );
    }

    public static EventFilter publishedOnly() {
        return empty();
    }
}

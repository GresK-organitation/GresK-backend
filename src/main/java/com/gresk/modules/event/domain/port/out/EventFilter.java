package com.gresk.modules.event.domain.port.out;

import com.gresk.modules.event.domain.model.EventStatus;
import com.gresk.modules.event.domain.model.Genre;

import java.time.LocalDate;
import java.util.Optional;

public record EventFilter(
        Optional<Genre>       genre,
        Optional<String>      city,
        Optional<LocalDate>   dateFrom,
        Optional<LocalDate>   dateTo,
        Optional<EventStatus> status
) {
    public static EventFilter empty() {
        return new EventFilter(
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

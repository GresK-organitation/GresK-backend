package com.gresk.modules.event.domain.model;

import com.gresk.shared.domain.MusicGenre;

import java.time.Instant;

public record CalendarEvent(
        EventId id,
        String title,
        Instant eventDate,
        EventStatus status,
        int soldPercentage,
        MusicGenre genre,
        String city,
        String venue
) {}

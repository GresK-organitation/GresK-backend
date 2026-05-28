package com.gresk.modules.event.application.dto;

public record CalendarEventResponse(
        String id,
        String title,
        String eventDate,
        String status,
        int soldPercentage,
        String genre,
        String city,
        String venue
) {}

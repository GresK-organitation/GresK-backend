package com.gresk.modules.event.application.dto;

import java.util.List;

public record CalendarEventsResponse(
        List<CalendarEventResponse> events,
        int totalCount
) {}

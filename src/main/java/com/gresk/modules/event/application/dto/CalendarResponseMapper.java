package com.gresk.modules.event.application.dto;

import com.gresk.modules.event.domain.model.CalendarEvent;
import com.gresk.modules.event.domain.model.HeatmapDay;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CalendarResponseMapper {

    public CalendarEventResponse toResponse(CalendarEvent event) {
        return new CalendarEventResponse(
                event.id().value().toString(),
                event.title(),
                event.eventDate() != null ? event.eventDate().toString() : null,
                event.status() != null ? event.status().name() : null,
                event.soldPercentage(),
                event.genre() != null ? event.genre().name() : null,
                event.city(),
                event.venue()
        );
    }

    public CalendarEventsResponse toCalendarResponse(List<CalendarEvent> events) {
        List<CalendarEventResponse> responses = events.stream().map(this::toResponse).toList();
        return new CalendarEventsResponse(responses, responses.size());
    }

    public HeatmapDayResponse toDayResponse(HeatmapDay day) {
        return new HeatmapDayResponse(
                day.date().toString(),
                day.eventCount(),
                day.avgOccupationPercentage()
        );
    }

    public HeatmapResponse toHeatmapResponse(int year, int month, List<HeatmapDay> days) {
        List<HeatmapDayResponse> dayResponses = days.stream().map(this::toDayResponse).toList();
        return new HeatmapResponse(year, month, dayResponses);
    }
}

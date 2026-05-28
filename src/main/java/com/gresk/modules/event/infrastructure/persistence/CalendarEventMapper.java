package com.gresk.modules.event.infrastructure.persistence;

import com.gresk.modules.event.domain.model.CalendarEvent;
import com.gresk.modules.event.domain.model.EventId;
import org.springframework.stereotype.Component;

@Component
public class CalendarEventMapper {

    public CalendarEvent toDomain(EventEntity e) {
        int soldPercentage = 0;
        if (e.getTotalCapacity() != null && e.getTotalCapacity() > 0) {
            int available = e.getAvailableCapacity() != null ? e.getAvailableCapacity() : 0;
            soldPercentage = (int) Math.round(
                    (e.getTotalCapacity() - available) / (double) e.getTotalCapacity() * 100
            );
        }
        return new CalendarEvent(
                EventId.of(e.getId().toString()),
                e.getTitle(),
                e.getEventDate(),
                e.getStatus(),
                soldPercentage,
                e.getGenre(),
                e.getCity(),
                e.getVenue()
        );
    }
}

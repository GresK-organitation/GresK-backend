package com.gresk.modules.event.application.usecase;

import com.gresk.modules.event.application.query.HeatmapQuery;
import com.gresk.modules.event.domain.model.CalendarEvent;
import com.gresk.modules.event.domain.model.HeatmapDay;
import com.gresk.modules.event.domain.port.out.EventRepository;
import com.gresk.modules.promoter.domain.model.valueobject.PromoterId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GetHeatmapUseCase {

    private final EventRepository eventRepository;

    public List<HeatmapDay> execute(HeatmapQuery query) {
        YearMonth yearMonth = YearMonth.of(query.year(), query.month());
        var from = yearMonth.atDay(1).atStartOfDay(ZoneOffset.UTC).toInstant();
        var to   = yearMonth.plusMonths(1).atDay(1).atStartOfDay(ZoneOffset.UTC).toInstant();

        PromoterId promoterId = PromoterId.of(query.promoterId());
        List<CalendarEvent> events = eventRepository.findCalendarEventsByPromoter(promoterId, from, to);

        Map<LocalDate, List<CalendarEvent>> byDay = events.stream()
                .collect(Collectors.groupingBy(e ->
                        e.eventDate().atZone(ZoneOffset.UTC).toLocalDate()
                ));

        List<HeatmapDay> days = new ArrayList<>(yearMonth.lengthOfMonth());
        for (int day = 1; day <= yearMonth.lengthOfMonth(); day++) {
            LocalDate date = yearMonth.atDay(day);
            List<CalendarEvent> dayEvents = byDay.getOrDefault(date, List.of());
            int avgOccupation = dayEvents.isEmpty() ? 0 :
                    (int) Math.round(dayEvents.stream()
                            .mapToInt(CalendarEvent::soldPercentage)
                            .average()
                            .orElse(0));
            days.add(new HeatmapDay(date, dayEvents.size(), avgOccupation));
        }
        return days;
    }
}

package com.gresk.modules.agenda.application.dto;

import com.gresk.modules.agenda.domain.model.AgendaEntry;
import com.gresk.modules.agenda.domain.model.AgendaGresKEvent;
import com.gresk.modules.agenda.domain.model.RecurrenceRule;
import org.springframework.stereotype.Component;

import java.time.DayOfWeek;
import java.util.stream.Collectors;

@Component
public class AgendaResponseMapper {

    public AgendaEntryResponse toResponse(AgendaEntry e) {
        RecurrenceRule rule = e.getRecurrenceRule();
        return new AgendaEntryResponse(
                e.getId().toString(),
                e.getPromoterId().toString(),
                e.getType().name(),
                e.getTitle(),
                e.getDescription(),
                e.getStartAt()  != null ? e.getStartAt().toString()  : null,
                e.getEndAt()    != null ? e.getEndAt().toString()    : null,
                e.isAllDay(),
                e.isCompleted(),
                e.isCancelled(),
                e.getColor(),
                e.getLabel(),
                e.getLinkedEntity() != null ? e.getLinkedEntity().type().name()             : null,
                e.getLinkedEntity() != null ? e.getLinkedEntity().entityId().toString()     : null,
                rule != null ? rule.frequency().name()  : null,
                rule != null ? rule.interval()          : null,
                rule != null ? rule.count()             : null,
                rule != null && rule.until() != null ? rule.until().toString() : null,
                rule != null && !rule.byDay().isEmpty()
                        ? rule.byDay().stream().map(DayOfWeek::name).sorted().collect(Collectors.joining(","))
                        : null,
                rule != null ? rule.byMonthDay() : null,
                e.getSeriesId()       != null ? e.getSeriesId().toString()       : null,
                e.getExceptionDate()  != null ? e.getExceptionDate().toString()  : null,
                e.getReminderMinutesBefore(),
                e.getCreatedAt().toString()
        );
    }

    public AgendaViewItemResponse toViewItem(AgendaEntry e) {
        return new AgendaViewItemResponse(
                e.getId().toString(),
                "AGENDA",
                e.getType().name(),
                e.getTitle(),
                e.getStartAt()  != null ? e.getStartAt().toString()  : null,
                e.getEndAt()    != null ? e.getEndAt().toString()    : null,
                e.isAllDay(),
                e.isCompleted(),
                e.getColor(),
                e.getLabel(),
                null, null, null, 0,
                e.getLinkedEntity() != null ? e.getLinkedEntity().type().name()         : null,
                e.getLinkedEntity() != null ? e.getLinkedEntity().entityId().toString() : null,
                e.getSeriesId()     != null ? e.getSeriesId().toString()                : null,
                e.getReminderMinutesBefore()
        );
    }

    /** Proyecta una ocurrencia recurrente con la fecha ajustada. */
    public AgendaViewItemResponse toViewItemWithDate(AgendaEntry master, String occurrenceDate) {
        return new AgendaViewItemResponse(
                master.getId().toString(),
                "AGENDA",
                master.getType().name(),
                master.getTitle(),
                occurrenceDate,
                null,
                master.isAllDay(),
                master.isCompleted(),
                master.getColor(),
                master.getLabel(),
                null, null, null, 0,
                master.getLinkedEntity() != null ? master.getLinkedEntity().type().name()         : null,
                master.getLinkedEntity() != null ? master.getLinkedEntity().entityId().toString() : null,
                master.getId().toString(),
                master.getReminderMinutesBefore()
        );
    }

    public AgendaViewItemResponse toViewItemFromGresK(AgendaGresKEvent e) {
        return new AgendaViewItemResponse(
                e.eventId(),
                "GRESK_EVENT",
                "GRESK_EVENT",
                e.title(),
                e.eventDate() != null ? e.eventDate().toString() : null,
                null,
                false, false,
                null, null,
                e.genre(),
                e.city(),
                e.venue(),
                e.soldPercentage(),
                null, null, null, null
        );
    }
}

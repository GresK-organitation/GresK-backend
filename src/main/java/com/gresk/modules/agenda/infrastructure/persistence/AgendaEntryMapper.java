package com.gresk.modules.agenda.infrastructure.persistence;

import com.gresk.modules.agenda.domain.model.*;
import com.gresk.modules.promoter.domain.model.valueobject.PromoterId;
import org.springframework.stereotype.Component;

import java.time.DayOfWeek;
import java.time.Instant;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class AgendaEntryMapper {

    public AgendaEntry toDomain(AgendaEntryEntity e) {
        EntityReference linked = null;
        if (e.getLinkedEntityType() != null && e.getLinkedEntityId() != null) {
            linked = new EntityReference(e.getLinkedEntityType(), e.getLinkedEntityId());
        }

        RecurrenceRule rule = null;
        if (e.getRecurrenceFrequency() != null) {
            Set<DayOfWeek> byDay = parseDays(e.getRecurrenceByDay());
            rule = new RecurrenceRule(
                    e.getRecurrenceFrequency(),
                    e.getRecurrenceInterval() != null ? e.getRecurrenceInterval() : 1,
                    e.getRecurrenceCount(),
                    e.getRecurrenceUntil(),
                    byDay,
                    e.getRecurrenceByMonthDay()
            );
        }

        AgendaEntryId seriesId = e.getSeriesId() != null ? AgendaEntryId.of(e.getSeriesId()) : null;

        return AgendaEntry.reconstitute(
                AgendaEntryId.of(e.getId()),
                PromoterId.of(e.getPromoterId()),
                e.getCreatedAt(),
                e.getType(),
                e.getTitle(),
                e.getDescription(),
                e.getStartAt(),
                e.getEndAt(),
                e.isAllDay(),
                e.isCompleted(),
                e.isCancelled(),
                e.getColor(),
                e.getLabel(),
                linked,
                rule,
                seriesId,
                e.getExceptionDate(),
                e.getReminderMinutesBefore(),
                e.isReminderSent()
        );
    }

    public AgendaEntryEntity toEntity(AgendaEntry entry) {
        RecurrenceRule rule = entry.getRecurrenceRule();
        return AgendaEntryEntity.builder()
                .id(entry.getId().value())
                .promoterId(entry.getPromoterId().value())
                .type(entry.getType())
                .title(entry.getTitle())
                .description(entry.getDescription())
                .startAt(entry.getStartAt())
                .endAt(entry.getEndAt())
                .allDay(entry.isAllDay())
                .completed(entry.isCompleted())
                .cancelled(entry.isCancelled())
                .color(entry.getColor())
                .label(entry.getLabel())
                .linkedEntityType(entry.getLinkedEntity() != null ? entry.getLinkedEntity().type()     : null)
                .linkedEntityId(entry.getLinkedEntity()   != null ? entry.getLinkedEntity().entityId() : null)
                .recurrenceFrequency(rule  != null ? rule.frequency()    : null)
                .recurrenceInterval(rule   != null ? rule.interval()     : null)
                .recurrenceCount(rule      != null ? rule.count()        : null)
                .recurrenceUntil(rule      != null ? rule.until()        : null)
                .recurrenceByDay(rule      != null ? formatDays(rule.byDay()) : null)
                .recurrenceByMonthDay(rule != null ? rule.byMonthDay()   : null)
                .seriesId(entry.getSeriesId()      != null ? entry.getSeriesId().value()  : null)
                .exceptionDate(entry.getExceptionDate())
                .reminderMinutesBefore(entry.getReminderMinutesBefore())
                .reminderSent(entry.isReminderSent())
                .createdAt(entry.getCreatedAt() != null ? entry.getCreatedAt() : Instant.now())
                .updatedAt(Instant.now())
                .build();
    }

    // ── Helpers ──────────────────────────────────────────────────────────────

    private Set<DayOfWeek> parseDays(String byDay) {
        if (byDay == null || byDay.isBlank()) return Set.of();
        return Arrays.stream(byDay.split(","))
                .map(String::trim)
                .map(DayOfWeek::valueOf)
                .collect(Collectors.toSet());
    }

    private String formatDays(Set<DayOfWeek> days) {
        if (days == null || days.isEmpty()) return null;
        return days.stream()
                .map(DayOfWeek::name)
                .sorted()
                .collect(Collectors.joining(","));
    }
}

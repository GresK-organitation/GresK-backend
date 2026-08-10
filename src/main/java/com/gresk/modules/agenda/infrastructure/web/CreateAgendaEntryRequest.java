package com.gresk.modules.agenda.infrastructure.web;

import com.gresk.modules.agenda.domain.model.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.Instant;

public record CreateAgendaEntryRequest(
        @NotNull  EntryType type,
        @NotBlank String    title,
        String    description,
        Instant   startAt,
        Instant   endAt,
        boolean   allDay,
        String    color,
        String    label,
        // Vínculo opcional
        LinkedEntityType linkedEntityType,
        java.util.UUID   linkedEntityId,
        // Recurrencia opcional
        RecurrenceFrequency   recurrenceFrequency,
        Integer               recurrenceInterval,
        Integer               recurrenceCount,
        Instant               recurrenceUntil,
        java.util.Set<java.time.DayOfWeek> recurrenceByDay,
        Integer               recurrenceByMonthDay,
        // Recordatorio
        Integer   reminderMinutesBefore
) {}

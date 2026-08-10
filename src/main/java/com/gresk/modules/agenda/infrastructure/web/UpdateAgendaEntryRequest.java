package com.gresk.modules.agenda.infrastructure.web;

import com.gresk.modules.agenda.domain.model.LinkedEntityType;
import jakarta.validation.constraints.NotBlank;

import java.time.Instant;
import java.util.UUID;

public record UpdateAgendaEntryRequest(
        @NotBlank String  title,
        String    description,
        Instant   startAt,
        Instant   endAt,
        boolean   allDay,
        String    color,
        String    label,
        LinkedEntityType linkedEntityType,
        UUID             linkedEntityId,
        Integer   reminderMinutesBefore
) {}

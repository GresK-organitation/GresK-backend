package com.gresk.modules.agenda.application.command;

import com.gresk.modules.agenda.domain.model.EntityReference;
import com.gresk.modules.agenda.domain.model.UpdateScope;

import java.time.Instant;

public record UpdateAgendaEntryCommand(
        String      entryId,
        String      promoterId,
        UpdateScope scope,
        Instant     occurrenceDate,   // fecha de la ocurrencia a modificar (THIS_ONLY / THIS_AND_FOLLOWING)
        String      title,
        String      description,
        Instant     startAt,
        Instant     endAt,
        boolean     allDay,
        String      color,
        String      label,
        EntityReference linkedEntity,
        Integer     reminderMinutesBefore
) {}

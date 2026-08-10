package com.gresk.modules.agenda.application.command;

import com.gresk.modules.agenda.domain.model.EntryType;
import com.gresk.modules.agenda.domain.model.EntityReference;
import com.gresk.modules.agenda.domain.model.RecurrenceRule;

import java.time.Instant;

public record CreateAgendaEntryCommand(
        String      promoterId,
        EntryType   type,
        String      title,
        String      description,
        Instant     startAt,
        Instant     endAt,
        boolean     allDay,
        String      color,
        String      label,
        EntityReference linkedEntity,
        RecurrenceRule  recurrenceRule,
        Integer     reminderMinutesBefore
) {}

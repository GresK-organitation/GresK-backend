package com.gresk.modules.agenda.application.dto;

/**
 * Elemento unificado de la vista de agenda.
 * {@code source} discrimina el origen: "AGENDA" o "GRESK_EVENT".
 */
public record AgendaViewItemResponse(
        String  id,
        String  source,          // AGENDA | GRESK_EVENT
        String  type,            // TASK | APPOINTMENT | REMINDER | GRESK_EVENT
        String  title,
        String  startAt,
        String  endAt,
        boolean allDay,
        boolean completed,
        String  color,
        String  label,
        // Solo GRESK_EVENT
        String  genre,
        String  city,
        String  venue,
        int     soldPercentage,
        // Solo AGENDA
        String  linkedEntityType,
        String  linkedEntityId,
        String  seriesId,
        Integer reminderMinutesBefore
) {}

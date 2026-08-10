package com.gresk.modules.agenda.domain.model;

import java.time.Instant;

/**
 * Proyección de lectura de un evento GresK para su inclusión en la vista de agenda.
 * No es un agregado — es un read model que cruza el límite de módulos.
 */
public record AgendaGresKEvent(
        String eventId,
        String title,
        Instant eventDate,
        String status,
        String genre,
        String city,
        String venue,
        int soldPercentage
) {}

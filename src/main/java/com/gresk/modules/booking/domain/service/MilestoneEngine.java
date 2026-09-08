package com.gresk.modules.booking.domain.service;

import com.gresk.modules.booking.domain.model.valueobject.Milestone;
import com.gresk.modules.booking.domain.model.valueobject.MilestoneBlueprint;

import java.time.Instant;
import java.util.List;

/**
 * Motor de hitos automáticos: genera hitos concretos a partir de plantillas y los
 * recalcula cuando la fecha del evento de un {@code Booking} se mueve. Servicio de
 * dominio sin estado (no un motor de orquestación externo, ver benchmarking Camunda).
 */
public final class MilestoneEngine {

    private MilestoneEngine() {
    }

    public static List<Milestone> generate(Instant eventDate, List<MilestoneBlueprint> blueprints) {
        if (blueprints == null) return List.of();
        return blueprints.stream()
                .map(blueprint -> Milestone.schedule(blueprint, eventDate))
                .toList();
    }

    /** Los hitos {@code COMPLETED} quedan congelados (ver {@link Milestone#recalculate(Instant)}). */
    public static List<Milestone> recalculateAll(Instant newEventDate, List<Milestone> current) {
        if (current == null) return List.of();
        return current.stream()
                .map(milestone -> milestone.recalculate(newEventDate))
                .toList();
    }
}

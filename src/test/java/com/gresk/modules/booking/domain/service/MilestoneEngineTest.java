package com.gresk.modules.booking.domain.service;

import com.gresk.modules.booking.domain.model.MilestoneStatus;
import com.gresk.modules.booking.domain.model.MilestoneType;
import com.gresk.modules.booking.domain.model.valueobject.Milestone;
import com.gresk.modules.booking.domain.model.valueobject.MilestoneBlueprint;
import com.gresk.modules.booking.domain.model.valueobject.MilestoneOffset;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MilestoneEngineTest {

    @Test
    void generaHitosAplicandoElOffsetDeCadaBlueprint() {
        Instant eventDate = Instant.parse("2026-06-15T20:00:00Z");
        List<MilestoneBlueprint> blueprints = List.of(
                new MilestoneBlueprint(MilestoneType.PAYMENT, "Depósito", MilestoneOffset.beforeEvent(30)),
                new MilestoneBlueprint(MilestoneType.MARKETING_CAMPAIGN, "Campaña", MilestoneOffset.afterEvent(0)));

        List<Milestone> milestones = MilestoneEngine.generate(eventDate, blueprints);

        assertEquals(2, milestones.size());
        assertEquals(eventDate.minus(30, ChronoUnit.DAYS), milestones.get(0).dueDate());
        assertEquals(eventDate, milestones.get(1).dueDate());
    }

    @Test
    void recalcularRespetaElFreezeDeLosHitosCompletados() {
        Instant originalEventDate = Instant.parse("2026-06-15T20:00:00Z");
        Milestone pending = Milestone.schedule(
                new MilestoneBlueprint(MilestoneType.PAYMENT, "Depósito", MilestoneOffset.beforeEvent(30)), originalEventDate);
        Milestone completed = Milestone.schedule(
                new MilestoneBlueprint(MilestoneType.LICENSE, "Licencia", MilestoneOffset.beforeEvent(20)), originalEventDate)
                .complete("hecho");

        Instant newEventDate = originalEventDate.plus(15, ChronoUnit.DAYS);
        List<Milestone> recalculated = MilestoneEngine.recalculateAll(newEventDate, List.of(pending, completed));

        Milestone recalculatedPending = recalculated.get(0);
        Milestone recalculatedCompleted = recalculated.get(1);

        assertEquals(newEventDate.minus(30, ChronoUnit.DAYS), recalculatedPending.dueDate());
        assertEquals(MilestoneStatus.COMPLETED, recalculatedCompleted.status());
        assertEquals(completed.dueDate(), recalculatedCompleted.dueDate());
        assertTrue(recalculatedCompleted == completed || recalculatedCompleted.dueDate().equals(completed.dueDate()));
    }
}

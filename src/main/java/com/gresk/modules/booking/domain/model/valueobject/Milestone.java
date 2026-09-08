package com.gresk.modules.booking.domain.model.valueobject;

import com.gresk.modules.booking.domain.exception.MilestoneAlreadyCompletedException;
import com.gresk.modules.booking.domain.model.MilestoneStatus;
import com.gresk.modules.booking.domain.model.MilestoneType;

import java.time.Instant;
import java.util.UUID;

/**
 * Hito automático de un {@code Booking} (pago, licencia, envío de rider, campaña de marketing...).
 * Vive como entrada de la lista {@code Booking.milestones}, mutada vía el patrón
 * "reemplazo inmutable" (igual que {@code rider.ChecklistEntry}): cada método de transición
 * devuelve una nueva instancia en lugar de mutar el objeto.
 */
public record Milestone(
        UUID milestoneId,
        MilestoneType type,
        String title,
        MilestoneOffset offset,
        Instant dueDate,
        MilestoneStatus status,
        Instant completedAt,
        String notes
) {

    public Milestone {
        if (milestoneId == null) milestoneId = UUID.randomUUID();
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("Milestone title must not be blank");
        }
        if (offset == null) {
            throw new IllegalArgumentException("Milestone offset must not be null");
        }
        if (dueDate == null) {
            throw new IllegalArgumentException("Milestone dueDate must not be null");
        }
        if (status == null) {
            throw new IllegalArgumentException("Milestone status must not be null");
        }
    }

    public static Milestone schedule(MilestoneBlueprint blueprint, Instant eventDate) {
        return new Milestone(UUID.randomUUID(), blueprint.type(), blueprint.title(), blueprint.offset(),
                blueprint.offset().applyTo(eventDate), MilestoneStatus.PENDING, null, null);
    }

    public Milestone complete(String notes) {
        if (status == MilestoneStatus.COMPLETED) {
            throw new MilestoneAlreadyCompletedException(milestoneId.toString());
        }
        return new Milestone(milestoneId, type, title, offset, dueDate, MilestoneStatus.COMPLETED, Instant.now(), notes);
    }

    public Milestone reopen() {
        return new Milestone(milestoneId, type, title, offset, dueDate, MilestoneStatus.PENDING, null, notes);
    }

    public Milestone skip(String reason) {
        return new Milestone(milestoneId, type, title, offset, dueDate, MilestoneStatus.SKIPPED, completedAt, reason);
    }

    /**
     * Recalcula {@code dueDate} a partir de una nueva fecha de evento. Invariante clave:
     * un hito ya {@code COMPLETED} queda congelado y no se recalcula.
     */
    public Milestone recalculate(Instant newEventDate) {
        if (status == MilestoneStatus.COMPLETED) {
            return this;
        }
        return new Milestone(milestoneId, type, title, offset, offset.applyTo(newEventDate), status, completedAt, notes);
    }
}

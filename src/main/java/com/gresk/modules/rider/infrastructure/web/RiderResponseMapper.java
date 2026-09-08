package com.gresk.modules.rider.infrastructure.web;

import com.gresk.modules.rider.application.dto.PendingRiderDto;
import com.gresk.modules.rider.domain.model.EventRiderChecklist;
import com.gresk.modules.rider.domain.model.HospitalityRider;
import com.gresk.modules.rider.domain.model.RiderAlert;
import com.gresk.modules.rider.domain.model.RiderLineItem;
import com.gresk.modules.rider.domain.model.TechnicalRider;
import com.gresk.modules.rider.domain.model.valueobject.ChecklistEntry;
import com.gresk.modules.rider.domain.model.valueobject.EquipmentEquivalence;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class RiderResponseMapper {

    public RiderResponse toResponse(TechnicalRider rider) {
        return new RiderResponse(
                rider.getId().toString(),
                rider.getArtistId().toString(),
                rider.getPromoterId().value().toString(),
                rider.getName(),
                rider.getStatus().name(),
                rider.getVersion(),
                rider.getShareToken(),
                rider.getSoundCheckDurationMinutes(),
                rider.getSoundCheckNotes(),
                rider.getStageDimensions(),
                rider.getStageElements(),
                rider.getStaff(),
                rider.getLineItems().stream().map(this::toLineItemResponse).toList(),
                rider.getAdditionalNotes(),
                rider.getCreatedAt(),
                rider.getUpdatedAt()
        );
    }

    public HospitalityRiderResponse toResponse(HospitalityRider rider) {
        return new HospitalityRiderResponse(
                rider.getId().toString(),
                rider.getArtistId().toString(),
                rider.getPromoterId().value().toString(),
                rider.getName(),
                rider.getStatus().name(),
                rider.getVersion(),
                rider.getShareToken(),
                rider.getLineItems().stream().map(this::toLineItemResponse).toList(),
                rider.getAdditionalNotes(),
                rider.getCreatedAt(),
                rider.getUpdatedAt()
        );
    }

    public RiderLineItemResponse toLineItemResponse(RiderLineItem item) {
        RiderLineItemResponse.EquivalenceResponse equivalence = item.getEquivalence()
                .map(this::toEquivalenceResponse)
                .orElse(null);
        return new RiderLineItemResponse(
                item.getId(),
                item.getCategory().name(),
                item.getDescription(),
                item.getQuantity(),
                item.isRequired(),
                item.getAttributes(),
                item.getFulfillmentSource().name(),
                equivalence,
                item.getNotes()
        );
    }

    private RiderLineItemResponse.EquivalenceResponse toEquivalenceResponse(EquipmentEquivalence e) {
        return new RiderLineItemResponse.EquivalenceResponse(
                e.requestedSpec(), e.proposedAlternative(), e.status().name(),
                e.proposedBy().name(), e.notes(), e.proposedAt(), e.decidedAt());
    }

    public ChecklistResponse toChecklistResponse(EventRiderChecklist checklist) {
        List<ChecklistEntryResponse> entries = checklist.getItems().stream()
                .map(this::toEntryResponse)
                .toList();
        return new ChecklistResponse(
                checklist.getId().toString(),
                checklist.getEventId().toString(),
                checklist.getRiderId().toString(),
                checklist.completionPercent(),
                entries,
                checklist.getAlertSentAt(),
                checklist.getCreatedAt(),
                checklist.getUpdatedAt()
        );
    }

    public RiderAlertResponse toAlertResponse(RiderAlert alert) {
        return new RiderAlertResponse(
                alert.getId().toString(),
                alert.getPromoterId().value().toString(),
                alert.getEventId().toString(),
                alert.getRiderId().toString(),
                alert.getMessage(),
                alert.isRead(),
                alert.getCreatedAt()
        );
    }

    public PendingRiderResponse toPendingResponse(PendingRiderDto dto) {
        return new PendingRiderResponse(
                dto.eventId(),
                dto.eventTitle(),
                dto.eventDate(),
                dto.riderId(),
                dto.riderName(),
                dto.unconfirmedRequiredCount(),
                dto.totalRequiredCount(),
                dto.completionPercent()
        );
    }

    private ChecklistEntryResponse toEntryResponse(ChecklistEntry entry) {
        return new ChecklistEntryResponse(
                entry.entryId(),
                entry.category().name(),
                entry.description(),
                entry.required(),
                entry.confirmed(),
                entry.confirmedAt(),
                entry.confirmedNotes()
        );
    }
}

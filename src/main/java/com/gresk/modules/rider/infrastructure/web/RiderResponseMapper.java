package com.gresk.modules.rider.infrastructure.web;

import com.gresk.modules.rider.application.dto.PendingRiderDto;
import com.gresk.modules.rider.domain.model.EventRiderChecklist;
import com.gresk.modules.rider.domain.model.RiderAlert;
import com.gresk.modules.rider.domain.model.TechnicalRider;
import com.gresk.modules.rider.domain.model.valueobject.ChecklistEntry;
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
                rider.getSoundSystem(),
                rider.getStageDimensions(),
                rider.getHospitality(),
                rider.getTransport(),
                rider.getStaff(),
                rider.getInputChannels(),
                rider.getBacklineItems(),
                rider.getStageElements(),
                rider.getAdditionalNotes(),
                rider.getCreatedAt(),
                rider.getUpdatedAt()
        );
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

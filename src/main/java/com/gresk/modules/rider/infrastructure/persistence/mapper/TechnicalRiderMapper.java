package com.gresk.modules.rider.infrastructure.persistence.mapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gresk.modules.artist.domain.model.valueobject.ArtistId;
import com.gresk.modules.promoter.domain.model.valueobject.PromoterId;
import com.gresk.modules.rider.domain.model.RiderId;
import com.gresk.modules.rider.domain.model.TechnicalRider;
import com.gresk.modules.rider.domain.model.valueobject.*;
import com.gresk.modules.rider.infrastructure.persistence.entity.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class TechnicalRiderMapper {

    private final ObjectMapper objectMapper;

    public TechnicalRider toDomain(TechnicalRiderEntity e) {
        SoundSystemRequirements soundSystem = null;
        if (e.getConsoleBrand() != null || e.getConsoleChannels() != null) {
            soundSystem = new SoundSystemRequirements(
                    e.getConsoleBrand(), e.getConsoleChannels(), e.getMonitorMixes(),
                    e.getPaDescription(), e.getProcessorNotes());
        }

        StageDimensions stageDimensions = null;
        if (e.getStageWidthMeters() != null || e.getStageDepthMeters() != null) {
            stageDimensions = new StageDimensions(
                    e.getStageWidthMeters(), e.getStageDepthMeters(), e.getStageMinHeightMeters(),
                    e.getPowerOutlets(), e.isHasDrumRiser());
        }

        HospitalityRequirements hospitality = null;
        if (e.getDressingRoomCapacity() != null || e.getCateringNotes() != null) {
            hospitality = new HospitalityRequirements(
                    e.getDressingRoomCapacity(), e.getCateringNotes(),
                    e.getWaterBottlesOnStage(), e.getPassesCount());
        }

        TransportRequirements transport = null;
        if (e.getVehicleType() != null) {
            transport = new TransportRequirements(
                    e.getVehicleType(), e.getPassengerCapacity(), e.getTransportNotes());
        }

        List<StaffMember> staff = e.getStaff().stream()
                .map(s -> new StaffMember(s.getRole(), s.getName())).toList();

        List<InputChannel> inputChannels = e.getInputChannels().stream()
                .map(c -> new InputChannel(c.getChannelNumber(), c.getInstrument(),
                        c.getMicrophone(), c.getInserts(), c.getNotes())).toList();

        List<BacklineItem> backlineItems = e.getBacklineItems().stream()
                .map(b -> new BacklineItem(b.getCategory(), b.getDescription(),
                        b.getBrand(), b.getModel(), b.isRequired())).toList();

        List<StageElement> stageElements = deserializeStageElements(e.getStageElementsJson());

        return TechnicalRider.reconstitute(
                RiderId.of(e.getId()),
                ArtistId.of(e.getArtistId()),
                PromoterId.of(e.getPromoterId()),
                e.getName(), e.getStatus(), e.getVersion(),
                staff, e.getSoundCheckDurationMinutes(), e.getSoundCheckNotes(),
                inputChannels, soundSystem, backlineItems,
                stageDimensions, stageElements,
                hospitality, transport,
                e.getAdditionalNotes(), e.getShareToken(),
                e.getCreatedAt(), e.getUpdatedAt()
        );
    }

    public TechnicalRiderEntity toEntity(TechnicalRider r) {
        return TechnicalRiderEntity.builder()
                .id(r.getId().value())
                .artistId(r.getArtistId().value())
                .promoterId(r.getPromoterId().value())
                .name(r.getName())
                .status(r.getStatus())
                .version(r.getVersion())
                .soundCheckDurationMinutes(r.getSoundCheckDurationMinutes())
                .soundCheckNotes(r.getSoundCheckNotes())
                .consoleBrand(r.getSoundSystem() != null ? r.getSoundSystem().consoleBrand() : null)
                .consoleChannels(r.getSoundSystem() != null ? r.getSoundSystem().consoleChannels() : null)
                .monitorMixes(r.getSoundSystem() != null ? r.getSoundSystem().monitorMixes() : null)
                .paDescription(r.getSoundSystem() != null ? r.getSoundSystem().paDescription() : null)
                .processorNotes(r.getSoundSystem() != null ? r.getSoundSystem().processorNotes() : null)
                .stageWidthMeters(r.getStageDimensions() != null ? r.getStageDimensions().widthMeters() : null)
                .stageDepthMeters(r.getStageDimensions() != null ? r.getStageDimensions().depthMeters() : null)
                .stageMinHeightMeters(r.getStageDimensions() != null ? r.getStageDimensions().minHeightMeters() : null)
                .powerOutlets(r.getStageDimensions() != null ? r.getStageDimensions().powerOutlets() : null)
                .hasDrumRiser(r.getStageDimensions() != null && r.getStageDimensions().hasDrumRiser())
                .stageElementsJson(serializeStageElements(r.getStageElements()))
                .dressingRoomCapacity(r.getHospitality() != null ? r.getHospitality().dressingRoomCapacity() : null)
                .cateringNotes(r.getHospitality() != null ? r.getHospitality().cateringNotes() : null)
                .waterBottlesOnStage(r.getHospitality() != null ? r.getHospitality().waterBottlesOnStage() : null)
                .passesCount(r.getHospitality() != null ? r.getHospitality().passesCount() : null)
                .vehicleType(r.getTransport() != null ? r.getTransport().vehicleType() : null)
                .passengerCapacity(r.getTransport() != null ? r.getTransport().passengerCapacity() : null)
                .transportNotes(r.getTransport() != null ? r.getTransport().notes() : null)
                .additionalNotes(r.getAdditionalNotes())
                .shareToken(r.getShareToken())
                .staff(r.getStaff().stream()
                        .map(s -> StaffMemberEmbeddable.builder().role(s.role()).name(s.name()).build())
                        .collect(java.util.stream.Collectors.toCollection(java.util.ArrayList::new)))
                .inputChannels(r.getInputChannels().stream()
                        .map(c -> InputChannelEmbeddable.builder()
                                .channelNumber(c.channelNumber()).instrument(c.instrument())
                                .microphone(c.microphone()).inserts(c.inserts()).notes(c.notes()).build())
                        .collect(java.util.stream.Collectors.toCollection(java.util.ArrayList::new)))
                .backlineItems(r.getBacklineItems().stream()
                        .map(b -> BacklineItemEmbeddable.builder()
                                .category(b.category()).description(b.description())
                                .brand(b.brand()).model(b.model()).required(b.required()).build())
                        .collect(java.util.stream.Collectors.toCollection(java.util.ArrayList::new)))
                .createdAt(r.getCreatedAt())
                .updatedAt(r.getUpdatedAt())
                .build();
    }

    private String serializeStageElements(List<StageElement> elements) {
        if (elements == null || elements.isEmpty()) return null;
        try {
            return objectMapper.writeValueAsString(elements);
        } catch (JsonProcessingException e) {
            return null;
        }
    }

    private List<StageElement> deserializeStageElements(String json) {
        if (json == null || json.isBlank()) return List.of();
        try {
            return objectMapper.readValue(json, new TypeReference<>() {});
        } catch (JsonProcessingException e) {
            return List.of();
        }
    }
}

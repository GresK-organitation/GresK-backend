package com.gresk.modules.rider.infrastructure.persistence.mapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gresk.modules.artist.domain.model.valueobject.ArtistId;
import com.gresk.modules.promoter.domain.model.valueobject.PromoterId;
import com.gresk.modules.rider.domain.model.RiderId;
import com.gresk.modules.rider.domain.model.RiderLineItem;
import com.gresk.modules.rider.domain.model.TechnicalRider;
import com.gresk.modules.rider.domain.model.valueobject.*;
import com.gresk.modules.rider.infrastructure.persistence.entity.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class TechnicalRiderMapper {

    private final ObjectMapper objectMapper;

    public TechnicalRider toDomain(TechnicalRiderEntity e) {
        StageDimensions stageDimensions = null;
        if (e.getStageWidthMeters() != null || e.getStageDepthMeters() != null) {
            stageDimensions = new StageDimensions(
                    e.getStageWidthMeters(), e.getStageDepthMeters(), e.getStageMinHeightMeters(),
                    e.getPowerOutlets(), e.isHasDrumRiser());
        }

        List<StaffMember> staff = e.getStaff().stream()
                .map(s -> new StaffMember(s.getRole(), s.getName())).toList();

        List<StageElement> stageElements = deserializeStageElements(e.getStageElementsJson());

        List<RiderLineItem> lineItems = e.getLineItems().stream().map(this::toDomainLineItem).toList();

        return TechnicalRider.reconstitute(
                RiderId.of(e.getId()),
                ArtistId.of(e.getArtistId()),
                PromoterId.of(e.getPromoterId()),
                e.getName(), e.getStatus(), e.getVersion(),
                staff, e.getSoundCheckDurationMinutes(), e.getSoundCheckNotes(),
                stageDimensions, stageElements, lineItems,
                e.getAdditionalNotes(), e.getShareToken(),
                e.getCreatedAt(), e.getUpdatedAt()
        );
    }

    public TechnicalRiderEntity toEntity(TechnicalRider r) {
        TechnicalRiderEntity entity = TechnicalRiderEntity.builder()
                .id(r.getId().value())
                .artistId(r.getArtistId().value())
                .promoterId(r.getPromoterId().value())
                .name(r.getName())
                .status(r.getStatus())
                .version(r.getVersion())
                .soundCheckDurationMinutes(r.getSoundCheckDurationMinutes())
                .soundCheckNotes(r.getSoundCheckNotes())
                .stageWidthMeters(r.getStageDimensions() != null ? r.getStageDimensions().widthMeters() : null)
                .stageDepthMeters(r.getStageDimensions() != null ? r.getStageDimensions().depthMeters() : null)
                .stageMinHeightMeters(r.getStageDimensions() != null ? r.getStageDimensions().minHeightMeters() : null)
                .powerOutlets(r.getStageDimensions() != null ? r.getStageDimensions().powerOutlets() : null)
                .hasDrumRiser(r.getStageDimensions() != null && r.getStageDimensions().hasDrumRiser())
                .stageElementsJson(serializeStageElements(r.getStageElements()))
                .additionalNotes(r.getAdditionalNotes())
                .shareToken(r.getShareToken())
                .staff(r.getStaff().stream()
                        .map(s -> StaffMemberEmbeddable.builder().role(s.role()).name(s.name()).build())
                        .collect(java.util.stream.Collectors.toCollection(ArrayList::new)))
                .createdAt(r.getCreatedAt())
                .updatedAt(r.getUpdatedAt())
                .build();

        List<TechnicalRiderLineItemEntity> lineItems = new ArrayList<>();
        for (RiderLineItem item : r.getLineItems()) {
            lineItems.add(toEntityLineItem(item, entity));
        }
        entity.setLineItems(lineItems);

        return entity;
    }

    private RiderLineItem toDomainLineItem(TechnicalRiderLineItemEntity e) {
        return RiderLineItem.reconstitute(
                e.getId(), e.getCategory(), e.getDescription(), e.getQuantity(), e.isRequired(),
                RiderLineItemAttributeCodec.deserializeAttributes(objectMapper, e.getAttributesJson()),
                e.getFulfillmentSource(),
                RiderLineItemAttributeCodec.toEquivalence(e.getEquivRequestedSpec(), e.getEquivProposedAlternative(),
                        e.getEquivStatus(), e.getEquivProposedBy(), e.getEquivNotes(),
                        e.getEquivProposedAt(), e.getEquivDecidedAt()),
                e.getNotes());
    }

    private TechnicalRiderLineItemEntity toEntityLineItem(RiderLineItem item, TechnicalRiderEntity parent) {
        TechnicalRiderLineItemEntity.TechnicalRiderLineItemEntityBuilder builder = TechnicalRiderLineItemEntity.builder()
                .id(item.getId())
                .rider(parent)
                .category(item.getCategory())
                .description(item.getDescription())
                .quantity(item.getQuantity())
                .required(item.isRequired())
                .attributesJson(RiderLineItemAttributeCodec.serializeAttributes(objectMapper, item.getAttributes()))
                .fulfillmentSource(item.getFulfillmentSource())
                .notes(item.getNotes());

        item.getEquivalence().ifPresent(eq -> builder
                .equivRequestedSpec(eq.requestedSpec())
                .equivProposedAlternative(eq.proposedAlternative())
                .equivStatus(eq.status().name())
                .equivProposedBy(eq.proposedBy().name())
                .equivNotes(eq.notes())
                .equivProposedAt(eq.proposedAt())
                .equivDecidedAt(eq.decidedAt()));

        return builder.build();
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

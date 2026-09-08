package com.gresk.modules.rider.infrastructure.persistence.mapper;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gresk.modules.artist.domain.model.valueobject.ArtistId;
import com.gresk.modules.promoter.domain.model.valueobject.PromoterId;
import com.gresk.modules.rider.domain.model.HospitalityRider;
import com.gresk.modules.rider.domain.model.RiderId;
import com.gresk.modules.rider.domain.model.RiderLineItem;
import com.gresk.modules.rider.infrastructure.persistence.entity.HospitalityRiderEntity;
import com.gresk.modules.rider.infrastructure.persistence.entity.HospitalityRiderLineItemEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class HospitalityRiderMapper {

    private final ObjectMapper objectMapper;

    public HospitalityRider toDomain(HospitalityRiderEntity e) {
        List<RiderLineItem> lineItems = e.getLineItems().stream().map(this::toDomainLineItem).toList();

        return HospitalityRider.reconstitute(
                RiderId.of(e.getId()),
                ArtistId.of(e.getArtistId()),
                PromoterId.of(e.getPromoterId()),
                e.getName(), e.getStatus(), e.getVersion(),
                lineItems, e.getAdditionalNotes(), e.getShareToken(),
                e.getCreatedAt(), e.getUpdatedAt()
        );
    }

    public HospitalityRiderEntity toEntity(HospitalityRider r) {
        HospitalityRiderEntity entity = HospitalityRiderEntity.builder()
                .id(r.getId().value())
                .artistId(r.getArtistId().value())
                .promoterId(r.getPromoterId().value())
                .name(r.getName())
                .status(r.getStatus())
                .version(r.getVersion())
                .additionalNotes(r.getAdditionalNotes())
                .shareToken(r.getShareToken())
                .createdAt(r.getCreatedAt())
                .updatedAt(r.getUpdatedAt())
                .build();

        List<HospitalityRiderLineItemEntity> lineItems = new ArrayList<>();
        for (RiderLineItem item : r.getLineItems()) {
            lineItems.add(toEntityLineItem(item, entity));
        }
        entity.setLineItems(lineItems);

        return entity;
    }

    private RiderLineItem toDomainLineItem(HospitalityRiderLineItemEntity e) {
        return RiderLineItem.reconstitute(
                e.getId(), e.getCategory(), e.getDescription(), e.getQuantity(), e.isRequired(),
                RiderLineItemAttributeCodec.deserializeAttributes(objectMapper, e.getAttributesJson()),
                e.getFulfillmentSource(),
                RiderLineItemAttributeCodec.toEquivalence(e.getEquivRequestedSpec(), e.getEquivProposedAlternative(),
                        e.getEquivStatus(), e.getEquivProposedBy(), e.getEquivNotes(),
                        e.getEquivProposedAt(), e.getEquivDecidedAt()),
                e.getNotes());
    }

    private HospitalityRiderLineItemEntity toEntityLineItem(RiderLineItem item, HospitalityRiderEntity parent) {
        HospitalityRiderLineItemEntity.HospitalityRiderLineItemEntityBuilder builder = HospitalityRiderLineItemEntity.builder()
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
}

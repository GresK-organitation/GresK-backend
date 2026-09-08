package com.gresk.modules.venue.infrastructure.persistence.mapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gresk.modules.promoter.domain.model.valueobject.PromoterId;
import com.gresk.modules.venue.domain.model.Venue;
import com.gresk.modules.venue.domain.model.VenueId;
import com.gresk.modules.venue.domain.model.valueobject.CapacityConfiguration;
import com.gresk.modules.venue.domain.model.valueobject.CurfewPolicy;
import com.gresk.modules.venue.domain.model.valueobject.EvacuationPlan;
import com.gresk.modules.venue.domain.model.valueobject.LoadingDockSpec;
import com.gresk.modules.venue.domain.model.valueobject.MunicipalLicense;
import com.gresk.modules.venue.infrastructure.persistence.entity.VenueEntity;
import com.gresk.shared.domain.valueobject.Address;
import com.gresk.shared.domain.valueobject.AssetId;
import com.gresk.shared.domain.valueobject.City;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.util.List;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class VenueMapper {

    private final ObjectMapper objectMapper;

    public Venue toDomain(VenueEntity e) {
        Address address = new Address(e.getAddressStreet(), City.of(e.getAddressCity()), e.getAddressCountry());

        CurfewPolicy curfew = e.getCurfewHardCutoff() == null ? null : new CurfewPolicy(
                e.getCurfewHardCutoff(), e.getCurfewMaxDecibels(),
                deserialize(e.getCurfewRestrictedDaysJson(), new TypeReference<Set<DayOfWeek>>() {}),
                e.getCurfewNotes());

        EvacuationPlan evacuationPlan = e.getEvacDocumentAssetId() == null ? null : new EvacuationPlan(
                AssetId.of(e.getEvacDocumentAssetId()), e.getEvacCertifiedCapacity(),
                e.getEvacLastReviewedAt(), e.getEvacReviewedBy());

        LoadingDockSpec dockSpec = e.getDockAccessHeightM() == null ? null : new LoadingDockSpec(
                e.getDockAccessHeightM().doubleValue(), e.getDockAccessWidthM().doubleValue(),
                e.getDockMaxVehicleKg().doubleValue(), e.getDockWindowStart(), e.getDockWindowEnd(),
                e.getDockCount(), e.getDockNotes());

        return Venue.reconstitute(
                new VenueId(e.getId()), PromoterId.of(e.getOwnerId()), e.getCreatedAt(),
                e.getName(), address,
                deserialize(e.getCapacityConfigurationsJson(), new TypeReference<List<CapacityConfiguration>>() {}),
                curfew, evacuationPlan,
                deserialize(e.getLicensesJson(), new TypeReference<List<MunicipalLicense>>() {}),
                dockSpec, e.isActive(), e.getUpdatedAt()
        );
    }

    public VenueEntity toEntity(Venue v) {
        CurfewPolicy curfew = v.getCurfewPolicy();
        EvacuationPlan evac = v.getEvacuationPlan();
        LoadingDockSpec dock = v.getLoadingDockSpec();

        return VenueEntity.builder()
                .id(v.getId().value())
                .ownerId(v.getOwnerId().value())
                .name(v.getName())
                .addressStreet(v.getAddress().street())
                .addressCity(v.getAddress().city().value())
                .addressCountry(v.getAddress().country())
                .capacityConfigurationsJson(serialize(v.getCapacityConfigurations()))
                .curfewHardCutoff(curfew != null ? curfew.hardCutoff() : null)
                .curfewMaxDecibels(curfew != null ? curfew.maxDecibels() : null)
                .curfewRestrictedDaysJson(curfew != null ? serialize(curfew.restrictedDays()) : null)
                .curfewNotes(curfew != null ? curfew.notes() : null)
                .evacDocumentAssetId(evac != null ? evac.documentAsset().value() : null)
                .evacCertifiedCapacity(evac != null ? evac.certifiedCapacity() : null)
                .evacLastReviewedAt(evac != null ? evac.lastReviewedAt() : null)
                .evacReviewedBy(evac != null ? evac.reviewedBy() : null)
                .licensesJson(serialize(v.getLicenses()))
                .dockAccessHeightM(dock != null ? BigDecimal.valueOf(dock.accessHeightMeters()) : null)
                .dockAccessWidthM(dock != null ? BigDecimal.valueOf(dock.accessWidthMeters()) : null)
                .dockMaxVehicleKg(dock != null ? BigDecimal.valueOf(dock.maxVehicleWeightKg()) : null)
                .dockWindowStart(dock != null ? dock.windowStart() : null)
                .dockWindowEnd(dock != null ? dock.windowEnd() : null)
                .dockCount(dock != null ? dock.dockCount() : null)
                .dockNotes(dock != null ? dock.notes() : null)
                .active(v.isActive())
                .createdAt(v.getCreatedAt())
                .updatedAt(v.getUpdatedAt())
                .build();
    }

    private String serialize(Object value) {
        try {
            return objectMapper.writeValueAsString(value);
        } catch (JsonProcessingException e) {
            return "[]";
        }
    }

    private <T> T deserialize(String json, TypeReference<T> type) {
        if (json == null || json.isBlank()) {
            return emptyOf(type);
        }
        try {
            return objectMapper.readValue(json, type);
        } catch (JsonProcessingException e) {
            return emptyOf(type);
        }
    }

    @SuppressWarnings("unchecked")
    private <T> T emptyOf(TypeReference<T> type) {
        return type.getType().getTypeName().startsWith("java.util.Set") ? (T) Set.of() : (T) List.of();
    }
}

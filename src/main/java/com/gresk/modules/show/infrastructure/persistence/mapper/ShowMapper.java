package com.gresk.modules.show.infrastructure.persistence.mapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gresk.modules.promoter.domain.model.valueobject.PromoterId;
import com.gresk.modules.show.domain.model.Show;
import com.gresk.modules.show.domain.model.ShowId;
import com.gresk.modules.show.domain.model.ShowStatus;
import com.gresk.modules.show.domain.model.valueobject.CostLineItem;
import com.gresk.modules.show.domain.model.valueobject.FinancialSimulation;
import com.gresk.modules.show.domain.model.valueobject.HoldWindow;
import com.gresk.modules.show.domain.model.valueobject.RevenueAssumptions;
import com.gresk.modules.show.domain.model.valueobject.SettlementSummary;
import com.gresk.modules.show.domain.model.valueobject.VenueBooking;
import com.gresk.modules.show.infrastructure.persistence.entity.ShowEntity;
import com.gresk.modules.venue.domain.model.VenueId;
import com.gresk.shared.domain.valueobject.Percentage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ShowMapper {

    private final ObjectMapper objectMapper;

    public Show toDomain(ShowEntity e) {
        VenueBooking venueBooking = e.getVenueId() == null ? null : new VenueBooking(
                new VenueId(e.getVenueId()), e.getVenueName(),
                e.getCapacityConfigCode(), e.getCapacityConfigLabel(), e.getConfirmedCapacity());

        HoldWindow holdWindow = e.getHoldPlacedAt() == null ? null
                : new HoldWindow(e.getHoldPlacedAt(), e.getHoldExpiresAt());

        FinancialSimulation simulation = e.getAvgTicketPrice() == null ? null : new FinancialSimulation(
                deserializeCostLineItems(e.getCostLineItemsJson()),
                new RevenueAssumptions(e.getAvgTicketPrice(), Percentage.of(e.getExpectedSelloutPercent()),
                        e.getSimulationCurrency()));

        SettlementSummary settlement = e.getSettlementSettledAt() == null ? null : new SettlementSummary(
                e.getSettlementActualAttendance(), e.getSettlementActualRevenue(),
                e.getSettlementActualCosts(), e.getSettlementNetResult(), e.getSettlementSettledAt());

        return Show.reconstitute(
                new ShowId(e.getId()), PromoterId.of(e.getPromoterId()), e.getCreatedAt(),
                e.getName(), e.getScheduledDate(), venueBooking, holdWindow, simulation,
                ShowStatus.valueOf(e.getStatus()), settlement, e.getLinkedMarketplaceEventId(),
                e.getCancellationReason(), e.getUpdatedAt()
        );
    }

    public ShowEntity toEntity(Show s) {
        VenueBooking venueBooking = s.getVenueBooking();
        HoldWindow holdWindow = s.getHoldWindow();
        FinancialSimulation simulation = s.getFinancialSimulation();
        SettlementSummary settlement = s.getSettlement();

        return ShowEntity.builder()
                .id(s.getId().value())
                .promoterId(s.getPromoterId().value())
                .name(s.getName())
                .scheduledDate(s.getScheduledDate())
                .status(s.getStatus().name())
                .venueId(venueBooking != null ? venueBooking.venueId().value() : null)
                .venueName(venueBooking != null ? venueBooking.venueName() : null)
                .capacityConfigCode(venueBooking != null ? venueBooking.capacityConfigCode() : null)
                .capacityConfigLabel(venueBooking != null ? venueBooking.capacityConfigLabel() : null)
                .confirmedCapacity(venueBooking != null ? venueBooking.confirmedCapacity() : null)
                .holdPlacedAt(holdWindow != null ? holdWindow.placedAt() : null)
                .holdExpiresAt(holdWindow != null ? holdWindow.expiresAt() : null)
                .costLineItemsJson(simulation != null ? serialize(simulation.costLineItems()) : null)
                .avgTicketPrice(simulation != null ? simulation.revenueAssumptions().avgTicketPrice() : null)
                .expectedSelloutPercent(simulation != null ? simulation.revenueAssumptions().expectedSelloutPercent().value() : null)
                .simulationCurrency(simulation != null ? simulation.revenueAssumptions().currency() : null)
                .settlementActualAttendance(settlement != null ? settlement.actualAttendance() : null)
                .settlementActualRevenue(settlement != null ? settlement.actualRevenue() : null)
                .settlementActualCosts(settlement != null ? settlement.actualCosts() : null)
                .settlementNetResult(settlement != null ? settlement.netResult() : null)
                .settlementSettledAt(settlement != null ? settlement.settledAt() : null)
                .linkedMarketplaceEventId(s.getLinkedMarketplaceEventId())
                .cancellationReason(s.getCancellationReason())
                .createdAt(s.getCreatedAt())
                .updatedAt(s.getUpdatedAt())
                .build();
    }

    private String serialize(List<CostLineItem> items) {
        try {
            return objectMapper.writeValueAsString(items);
        } catch (JsonProcessingException e) {
            return "[]";
        }
    }

    private List<CostLineItem> deserializeCostLineItems(String json) {
        if (json == null || json.isBlank()) return List.of();
        try {
            return objectMapper.readValue(json, new TypeReference<>() {});
        } catch (JsonProcessingException e) {
            return List.of();
        }
    }
}

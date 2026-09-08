package com.gresk.modules.show.infrastructure.web;

import com.gresk.modules.show.domain.model.Show;
import com.gresk.modules.show.domain.model.ShowLogEntry;
import com.gresk.modules.show.domain.model.valueobject.FinancialSimulation;
import com.gresk.modules.show.domain.model.valueobject.FinancialViabilityReport;
import com.gresk.modules.show.domain.model.valueobject.HoldWindow;
import com.gresk.modules.show.domain.model.valueobject.SettlementSummary;
import com.gresk.modules.show.domain.model.valueobject.VenueBooking;
import com.gresk.modules.show.infrastructure.web.dto.FinancialViabilityReportResponse;
import com.gresk.modules.show.infrastructure.web.dto.ShowLogEntryResponse;
import com.gresk.modules.show.infrastructure.web.dto.ShowResponse;
import com.gresk.shared.domain.valueobject.AssetId;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
public class ShowResponseMapper {

    public ShowResponse toResponse(Show s) {
        VenueBooking venueBooking = s.getVenueBooking();
        HoldWindow holdWindow = s.getHoldWindow();
        FinancialSimulation simulation = s.getFinancialSimulation();
        SettlementSummary settlement = s.getSettlement();

        return new ShowResponse(
                s.getId().toString(),
                s.getPromoterId().toString(),
                s.getName(),
                s.getScheduledDate(),
                s.getStatus(),
                venueBooking == null ? null : new ShowResponse.VenueBookingResponse(
                        venueBooking.venueId().toString(), venueBooking.venueName(),
                        venueBooking.capacityConfigCode(), venueBooking.capacityConfigLabel(),
                        venueBooking.confirmedCapacity()),
                holdWindow == null ? null : new ShowResponse.HoldWindowResponse(
                        holdWindow.placedAt(), holdWindow.expiresAt(), holdWindow.isExpired(Instant.now())),
                simulation == null ? null : new ShowResponse.FinancialSimulationResponse(
                        simulation.costLineItems().stream()
                                .map(i -> new ShowResponse.CostLineItemResponse(i.category(), i.label(), i.amount(), i.nature()))
                                .toList(),
                        simulation.revenueAssumptions().avgTicketPrice(),
                        simulation.revenueAssumptions().expectedSelloutPercent().value(),
                        simulation.revenueAssumptions().currency()),
                settlement == null ? null : new ShowResponse.SettlementResponse(
                        settlement.actualAttendance(), settlement.actualRevenue(), settlement.actualCosts(),
                        settlement.netResult(), settlement.settledAt()),
                s.getLinkedMarketplaceEventId() == null ? null : s.getLinkedMarketplaceEventId().toString(),
                s.getCancellationReason(),
                s.getCreatedAt(),
                s.getUpdatedAt()
        );
    }

    public FinancialViabilityReportResponse toResponse(FinancialViabilityReport r) {
        return new FinancialViabilityReportResponse(
                r.totalFixedCosts(), r.variableCostPerAttendee(), r.contributionMarginPerTicket(),
                r.breakEvenAttendees(), r.breakEvenPercentOfCapacity(), r.projectedAttendance(),
                r.projectedRevenue(), r.projectedProfit(), r.marginOfSafetyPercent(), r.viable());
    }

    public ShowLogEntryResponse toResponse(ShowLogEntry entry) {
        return new ShowLogEntryResponse(
                entry.getId().toString(), entry.getShowId().toString(), entry.getType(), entry.getActor(),
                entry.getOccurredAt(), entry.getDescription(), entry.getRelatedParty(),
                entry.getAttachments().stream().map(AssetId::value).toList());
    }
}

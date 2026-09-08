package com.gresk.modules.show.infrastructure.web.dto;

import com.gresk.modules.show.domain.model.ShowStatus;
import com.gresk.modules.show.domain.model.valueobject.CostCategory;
import com.gresk.modules.show.domain.model.valueobject.CostNature;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public record ShowResponse(
        String     id,
        String     promoterId,
        String     name,
        Instant    scheduledDate,
        ShowStatus status,
        VenueBookingResponse venueBooking,
        HoldWindowResponse   holdWindow,
        FinancialSimulationResponse financialSimulation,
        SettlementResponse settlement,
        String     linkedMarketplaceEventId,
        String     cancellationReason,
        Instant    createdAt,
        Instant    updatedAt
) {
    public record VenueBookingResponse(String venueId, String venueName, String capacityConfigCode,
                                        String capacityConfigLabel, int confirmedCapacity) {}

    public record HoldWindowResponse(Instant placedAt, Instant expiresAt, boolean expired) {}

    public record CostLineItemResponse(CostCategory category, String label, BigDecimal amount, CostNature nature) {}

    public record FinancialSimulationResponse(List<CostLineItemResponse> costLineItems, BigDecimal avgTicketPrice,
                                               int expectedSelloutPercent, String currency) {}

    public record SettlementResponse(int actualAttendance, BigDecimal actualRevenue, BigDecimal actualCosts,
                                      BigDecimal netResult, Instant settledAt) {}
}

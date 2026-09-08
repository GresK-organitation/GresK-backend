package com.gresk.modules.show.application.command;

import java.math.BigDecimal;
import java.util.List;

public record RunViabilitySimulationCommand(
        String showId,
        String promoterId,
        List<CostLineItemInput> costLineItems,
        BigDecimal avgTicketPrice,
        int expectedSelloutPercent,
        String currency
) {
}

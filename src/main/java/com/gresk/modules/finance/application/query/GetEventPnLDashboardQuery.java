package com.gresk.modules.finance.application.query;

public record GetEventPnLDashboardQuery(
        String linkedEventId,
        String promoterId
) {
}

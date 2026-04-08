package com.gresk.modules.promoter.application.port.in;

import com.gresk.modules.promoter.application.dto.PromoterDashboardDTO;

import java.util.UUID;

public interface GetPromoterDashboardPort {
    PromoterDashboardDTO execute(UUID accountId);
}

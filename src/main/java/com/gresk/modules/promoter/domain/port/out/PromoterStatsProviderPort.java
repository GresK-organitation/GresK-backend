package com.gresk.modules.promoter.domain.port.out;

import com.gresk.modules.promoter.domain.model.valueobject.PromoterId;
import com.gresk.modules.promoter.domain.model.valueobject.PromoterStats;

public interface PromoterStatsProviderPort {
    PromoterStats getStatsByPromoterId(PromoterId promoterId);
}

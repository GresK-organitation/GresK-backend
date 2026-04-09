package com.gresk.modules.promoter.infrastructure;

import com.gresk.modules.promoter.domain.model.valueobject.PromoterId;
import com.gresk.modules.promoter.domain.model.valueobject.PromoterStats;
import com.gresk.modules.promoter.domain.port.out.PromoterStatsProviderPort;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
@Component
public class PromoterStatsProviderAdapter implements PromoterStatsProviderPort {


    @Override
    public PromoterStats getStatsByPromoterId(PromoterId promoterId) {
        return new PromoterStats(
                new BigDecimal("15450.75"), // totalRevenue
                12L,                         // totalEvents
                4.8                          // averageRating
        );
    }

}

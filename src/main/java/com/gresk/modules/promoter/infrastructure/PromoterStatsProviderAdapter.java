package com.gresk.modules.promoter.infrastructure;

import com.gresk.modules.promoter.domain.model.valueobject.PromoterId;
import com.gresk.modules.promoter.domain.model.valueobject.PromoterStats;
import com.gresk.modules.promoter.domain.port.out.PromoterStatsProviderPort;
import com.gresk.modules.promoter.infrastructure.persitence.PromoterStatsQueryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
@RequiredArgsConstructor
public class PromoterStatsProviderAdapter implements PromoterStatsProviderPort {

    private final PromoterStatsQueryRepository statsRepository;

    @Override
    public PromoterStats getStatsByPromoterId(PromoterId promoterId) {
        var id = promoterId.value();

        BigDecimal totalRevenue  = nullSafe(statsRepository.getTotalRevenue(id));
        long       totalEvents   = nullSafeLong(statsRepository.getTotalEvents(id));
        long       totalAttendees = nullSafeLong(statsRepository.getTotalAttendees(id));
        double     sellThrough   = nullSafeDouble(statsRepository.getSellThrough(id));
        long       activeEvents  = nullSafeLong(statsRepository.getActiveEvents(id));
        long       pendingEvents = nullSafeLong(statsRepository.getPendingEvents(id));
        BigDecimal avgTicketPrice = nullSafe(statsRepository.getAvgTicketPrice(id));

        // averageRating permanece en 0.0 hasta que se implemente el módulo de ratings
        return new PromoterStats(
                totalRevenue,
                totalEvents,
                0.0,
                totalAttendees,
                sellThrough,
                activeEvents,
                pendingEvents,
                avgTicketPrice
        );
    }

    private BigDecimal nullSafe(BigDecimal value) {
        return value != null ? value : BigDecimal.ZERO;
    }

    private long nullSafeLong(Long value) {
        return value != null ? value : 0L;
    }

    private double nullSafeDouble(Double value) {
        return value != null ? value : 0.0;
    }
}

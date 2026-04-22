package com.gresk.modules.promoter.application.usecase;

import com.gresk.modules.promoter.application.dto.PromoterEventDTO;
import com.gresk.modules.promoter.application.port.in.GetPromoterEventsPort;
import com.gresk.modules.promoter.infrastructure.persitence.PromoterEventQueryRepository;
import com.gresk.modules.promoter.infrastructure.persitence.PromoterEventSummary;
import com.gresk.shared.domain.port.out.ImageUrlResolverPort;
import com.gresk.shared.domain.valueobject.AssetId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GetPromoterEventsUseCase implements GetPromoterEventsPort {

    private final PromoterEventQueryRepository eventQueryRepository;
    private final ImageUrlResolverPort         imageUrlResolver;

    @Override
    public List<PromoterEventDTO> execute(UUID accountId) {
        return eventQueryRepository
                .findAllWithMetricsByPromoterId(accountId)
                .stream()
                .map(this::toDTO)
                .toList();
    }

    private PromoterEventDTO toDTO(PromoterEventSummary s) {
        BigDecimal effectivePrice = s.getDiscountedAmount() != null
                ? s.getDiscountedAmount()
                : (s.getAmount() != null ? s.getAmount() : BigDecimal.ZERO);

        int  totalCapacity = s.getTotalCapacity() != null ? s.getTotalCapacity() : 0;
        long ticketsSold   = s.getTicketsSold()   != null ? s.getTicketsSold()   : 0L;

        BigDecimal revenue = effectivePrice.multiply(BigDecimal.valueOf(ticketsSold));

        double conversionRate = totalCapacity > 0
                ? (ticketsSold * 100.0 / totalCapacity)
                : 0.0;

        String coverImageUrl = null;
        String assetId = s.getCoverImageUrl(); // returns cover_image_asset_id value (aliased)
        if (assetId != null && !assetId.isBlank()) {
            coverImageUrl = imageUrlResolver.resolveOrDefault(AssetId.of(assetId));
        }
        double avgRating = s.getAvgOverallRating() != null ? s.getAvgOverallRating() : 0.0;

        return new PromoterEventDTO(
                s.getId().toString(),
                s.getTitle(),
                s.getEventDate() != null ? s.getEventDate().toString() : null,
                s.getVenue(),
                s.getCity(),
                s.getStatus(),
                totalCapacity,
                (int) ticketsSold,
                revenue,
                effectivePrice,
                s.getGenre(),
                coverImageUrl,
                conversionRate,
                avgRating
        );
    }
}

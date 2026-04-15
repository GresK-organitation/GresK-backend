package com.gresk.modules.promoter.application.usecase;

import com.gresk.modules.promoter.application.dto.PromoterEventDTO;
import com.gresk.modules.promoter.application.port.in.GetPromoterEventsPort;
import com.gresk.modules.promoter.infrastructure.persitence.PromoterEventQueryRepository;
import com.gresk.modules.event.infrastructure.persistence.EventEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GetPromoterEventsUseCase implements GetPromoterEventsPort {

    private final PromoterEventQueryRepository eventQueryRepository;

    @Override
    public List<PromoterEventDTO> execute(UUID accountId) {
        return eventQueryRepository.findAllByPromoterId(accountId)
                .stream()
                .map(this::toDTO)
                .toList();
    }

    private PromoterEventDTO toDTO(EventEntity e) {
        BigDecimal effectivePrice = e.getDiscountedAmount() != null
                ? e.getDiscountedAmount()
                : (e.getAmount() != null ? e.getAmount() : BigDecimal.ZERO);

        int totalCapacity     = e.getTotalCapacity() != null ? e.getTotalCapacity() : 0;
        int availableCapacity = e.getAvailableCapacity() != null ? e.getAvailableCapacity() : 0;
        int ticketsSold       = totalCapacity - availableCapacity;

        BigDecimal revenue = effectivePrice.multiply(BigDecimal.valueOf(ticketsSold));

        return new PromoterEventDTO(
                e.getId().toString(),
                e.getTitle(),
                e.getEventDate() != null ? e.getEventDate().toString() : null,
                e.getVenue(),
                e.getCity(),
                e.getStatus() != null ? e.getStatus().name() : null,
                totalCapacity,
                ticketsSold,
                revenue,
                effectivePrice,
                e.getGenre() != null ? e.getGenre().name() : null,
                e.getCoverImageUrl()
        );
    }
}

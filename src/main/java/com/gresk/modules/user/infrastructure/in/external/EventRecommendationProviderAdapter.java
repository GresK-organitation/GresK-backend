package com.gresk.modules.user.infrastructure.in.external;

import com.gresk.modules.event.domain.model.EventStatus;
import com.gresk.modules.event.infrastructure.persistence.EventEntity;
import com.gresk.modules.user.domain.model.EventRecommendation;
import com.gresk.modules.user.domain.port.out.EventRecommendationProvider;
import com.gresk.modules.user.infrastructure.persistence.UserEventQueryRepository;
import com.gresk.shared.domain.MusicGenre;
import com.gresk.shared.domain.valueobject.City;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.ZoneId;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class EventRecommendationProviderAdapter implements EventRecommendationProvider {

    private static final int MAX_RECOMMENDATIONS = 3;

    private final UserEventQueryRepository queryRepository;

    @Override
    public Set<EventRecommendation> getTopEvents(City city, Set<MusicGenre> musicGenres) {
        Instant     now  = Instant.now();
        PageRequest page = PageRequest.of(0, MAX_RECOMMENDATIONS);

        // 1. Búsqueda principal: ciudad del usuario + géneros preferidos
        List<EventEntity> results = queryRepository.findRecommendedEvents(
                city.value(), musicGenres, now, EventStatus.PUBLISHED, page
        );

        // 2. Fallback: sin filtro de ciudad, solo géneros
        if (results.isEmpty()) {
            results = queryRepository.findRecommendedEventsByGenres(
                    musicGenres, now, EventStatus.PUBLISHED, page
            );
        }

        return results.stream()
                .map(this::toRecommendation)
                .collect(Collectors.toSet());
    }

    private EventRecommendation toRecommendation(EventEntity e) {
        // Precio efectivo: descuento si existe, precio base si no
        BigDecimal effectivePrice = e.getDiscountedAmount() != null
                ? e.getDiscountedAmount()
                : e.getAmount();

        // Ubicación legible: nombre de sala si existe, ciudad si no
        String location = (e.getVenue() != null && !e.getVenue().isBlank())
                ? e.getVenue()
                : e.getCity();

        return new EventRecommendation(
                e.getId().toString(),
                e.getTitle(),
                location,
                e.getEventDate().atZone(ZoneId.systemDefault()).toLocalDateTime(),
                e.getCoverImageUrl(),
                e.getGenre() != null ? e.getGenre().name() : null,
                effectivePrice
        );
    }
}

package com.gresk.modules.user.application.dto;

import lombok.Builder;
import java.time.format.DateTimeFormatter;
import com.gresk.modules.user.domain.model.EventRecommendation;

@Builder
public record EventRecommendedDTO(
        String id,
        String title,
        String location,
        String date,
        String time,
        String imageUrl,
        String category
) {

    public static EventRecommendedDTO fromDomain(EventRecommendation domain, String defaultImageUrl) {
        String imageUrl = domain.imageUrl();
        if (imageUrl == null || imageUrl.isBlank()) {
            imageUrl = defaultImageUrl;
        }
        return EventRecommendedDTO.builder()
                .id(domain.eventId())
                .title(domain.title())
                .location(domain.location())
                .date(domain.dateTime().format(DateTimeFormatter.ISO_LOCAL_DATE))
                .time(domain.dateTime().format(DateTimeFormatter.ofPattern("HH:mm")))
                .imageUrl(imageUrl)
                .category(domain.category())
                .build();
    }
}
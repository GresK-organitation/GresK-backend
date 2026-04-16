package com.gresk.modules.user.application.dto;

import com.gresk.modules.user.domain.model.EventRecommendation;
import lombok.Builder;

import java.time.format.DateTimeFormatter;

@Builder
public record EventRecommendedDTO(
        String id,
        String title,
        String location,
        String date,
        String time,
        String imageUrl,
        String category,
        String price         // precio efectivo: "195.00"
) {

    public static EventRecommendedDTO fromDomain(EventRecommendation domain, String defaultImageUrl) {
        String imageUrl = (domain.imageUrl() == null || domain.imageUrl().isBlank())
                ? defaultImageUrl
                : domain.imageUrl();

        String price = domain.price() != null
                ? domain.price().stripTrailingZeros().toPlainString()
                : "0";

        return EventRecommendedDTO.builder()
                .id(domain.eventId())
                .title(domain.title())
                .location(domain.location())
                .date(domain.dateTime().format(DateTimeFormatter.ISO_LOCAL_DATE))
                .time(domain.dateTime().format(DateTimeFormatter.ofPattern("HH:mm")))
                .imageUrl(imageUrl)
                .category(domain.category())
                .price(price)
                .build();
    }
}
package com.gresk.modules.promoter.infrastructure.persitence;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

/**
 * Spring Data interface projection that combines event data with ticket sales
 * count (via LEFT JOIN tickets). Method names must match the SQL column aliases.
 */
public interface PromoterEventSummary {
    UUID       getId();
    String     getTitle();
    Instant    getEventDate();
    String     getVenue();
    String     getCity();
    String     getStatus();
    Integer    getTotalCapacity();
    BigDecimal getAmount();
    BigDecimal getDiscountedAmount();
    String     getGenre();
    String     getCoverImageUrl();
    Long       getTicketsSold();
}

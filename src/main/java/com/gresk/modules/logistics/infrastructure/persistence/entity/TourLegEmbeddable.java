package com.gresk.modules.logistics.infrastructure.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;

@Embeddable
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TourLegEmbeddable {

    @Column(name = "booking_id", nullable = false)
    private UUID bookingId;

    @Column(name = "sequence_order", nullable = false)
    private int sequenceOrder;

    @Column(name = "show_date", nullable = false)
    private LocalDate showDate;

    @Column(name = "venue_name", nullable = false)
    private String venueName;

    @Column(name = "venue_city")
    private String venueCity;
}

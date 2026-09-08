package com.gresk.modules.logistics.infrastructure.persistence.entity;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "itinerary_segments")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ItinerarySegmentEntity {

    @Id
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "itinerary_id", nullable = false)
    private ItineraryEntity itinerary;

    @Column(nullable = false)
    private String type;

    @Column(name = "departure_at", nullable = false)
    private Instant departureAt;

    @Column(name = "departure_location", nullable = false)
    private String departureLocation;

    @Column(name = "arrival_at")
    private Instant arrivalAt;

    @Column(name = "arrival_location", nullable = false)
    private String arrivalLocation;

    @Column(name = "carrier_or_operator")
    private String carrierOrOperator;

    @Column(name = "segment_code")
    private String segmentCode;

    @Column(name = "confirmation_reference")
    private String confirmationReference;

    @Column(name = "seat_or_capacity_info")
    private String seatOrCapacityInfo;

    @Column(name = "voucher_url")
    private String voucherUrl;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @Builder.Default
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "itinerary_segment_travelers", joinColumns = @JoinColumn(name = "segment_id"))
    @Column(name = "traveler_id")
    private Set<UUID> travelerIds = new HashSet<>();
}

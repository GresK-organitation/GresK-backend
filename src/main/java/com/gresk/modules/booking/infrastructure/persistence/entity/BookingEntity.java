package com.gresk.modules.booking.infrastructure.persistence.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "bookings")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingEntity {

    @Id
    private UUID id;

    @Column(name = "promoter_id", nullable = false)
    private UUID promoterId;

    @Column(name = "artist_id", nullable = false)
    private UUID artistId;

    @Column(name = "venue_id")
    private UUID venueId;

    @Column(name = "venue_name", nullable = false)
    private String venueName;

    @Column(name = "venue_country", nullable = false)
    private String venueCountry;

    @Column(name = "venue_region")
    private String venueRegion;

    @Column(name = "venue_city", nullable = false)
    private String venueCity;

    @Column(name = "venue_radius_km")
    private Double venueRadiusKm;

    @Column(name = "event_date", nullable = false)
    private Instant eventDate;

    @Column(name = "status", nullable = false)
    private String status;

    @Column(name = "hold_expires_at")
    private Instant holdExpiresAt;

    @Column(name = "exclusivity_country")
    private String exclusivityCountry;

    @Column(name = "exclusivity_region")
    private String exclusivityRegion;

    @Column(name = "exclusivity_city")
    private String exclusivityCity;

    @Column(name = "exclusivity_radius_km")
    private Double exclusivityRadiusKm;

    @Column(name = "exclusivity_days_before")
    private Integer exclusivityDaysBefore;

    @Column(name = "exclusivity_days_after")
    private Integer exclusivityDaysAfter;

    @Column(name = "day_sheet_show_date")
    private LocalDate daySheetShowDate;

    @Column(name = "linked_event_id")
    private UUID linkedEventId;

    @Column(name = "linked_contract_id")
    private UUID linkedContractId;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @Builder.Default
    @OneToMany(mappedBy = "booking", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BookingMilestoneEntity> milestones = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "booking", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BookingDaySheetEntryEntity> daySheetEntries = new ArrayList<>();
}

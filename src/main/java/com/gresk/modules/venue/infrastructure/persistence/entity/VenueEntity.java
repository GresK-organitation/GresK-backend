package com.gresk.modules.venue.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

@Entity
@Table(name = "venues")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VenueEntity {

    @Id
    @Column(columnDefinition = "UUID", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "owner_id", nullable = false)
    private UUID ownerId;

    @Column(nullable = false, length = 255)
    private String name;

    @Column(name = "address_street",  nullable = false, length = 255) private String addressStreet;
    @Column(name = "address_city",    nullable = false, length = 100) private String addressCity;
    @Column(name = "address_country", nullable = false, length = 100) private String addressCountry;

    @Column(name = "capacity_configurations", columnDefinition = "jsonb", nullable = false)
    @JdbcTypeCode(SqlTypes.JSON)
    private String capacityConfigurationsJson;

    @Column(name = "curfew_hard_cutoff")             private LocalTime curfewHardCutoff;
    @Column(name = "curfew_max_decibels")            private Integer   curfewMaxDecibels;
    @Column(name = "curfew_restricted_days", columnDefinition = "jsonb")
    @JdbcTypeCode(SqlTypes.JSON)
    private String curfewRestrictedDaysJson;
    @Column(name = "curfew_notes", columnDefinition = "TEXT") private String curfewNotes;

    @Column(name = "evac_document_asset_id", length = 512) private String    evacDocumentAssetId;
    @Column(name = "evac_certified_capacity")              private Integer   evacCertifiedCapacity;
    @Column(name = "evac_last_reviewed_at")                private LocalDate evacLastReviewedAt;
    @Column(name = "evac_reviewed_by", length = 255)       private String    evacReviewedBy;

    @Column(columnDefinition = "jsonb", nullable = false)
    @JdbcTypeCode(SqlTypes.JSON)
    private String licensesJson;

    @Column(name = "dock_access_height_m") private BigDecimal dockAccessHeightM;
    @Column(name = "dock_access_width_m")  private BigDecimal dockAccessWidthM;
    @Column(name = "dock_max_vehicle_kg")  private BigDecimal dockMaxVehicleKg;
    @Column(name = "dock_window_start")    private LocalTime  dockWindowStart;
    @Column(name = "dock_window_end")      private LocalTime  dockWindowEnd;
    @Column(name = "dock_count")           private Integer    dockCount;
    @Column(name = "dock_notes", columnDefinition = "TEXT") private String dockNotes;

    @Column(nullable = false) private boolean active;

    @Column(name = "created_at", nullable = false, updatable = false) private Instant createdAt;
    @Column(name = "updated_at", nullable = false)                    private Instant updatedAt;

    @PrePersist
    protected void onCreate() {
        Instant now = Instant.now();
        if (createdAt == null) createdAt = now;
        updatedAt = now;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = Instant.now();
    }
}

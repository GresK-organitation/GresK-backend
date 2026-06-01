package com.gresk.modules.rider.infrastructure.persistence.entity;

import com.gresk.modules.rider.domain.model.RiderStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "technical_riders")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TechnicalRiderEntity {

    @Id
    @Column(columnDefinition = "UUID", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "artist_id", nullable = false)
    private UUID artistId;

    @Column(name = "promoter_id", nullable = false)
    private UUID promoterId;

    @Column(nullable = false, length = 255)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private RiderStatus status;

    @Column(nullable = false)
    private int version;

    @Column(name = "sound_check_duration_minutes")
    private Integer soundCheckDurationMinutes;

    @Column(name = "sound_check_notes", columnDefinition = "TEXT")
    private String soundCheckNotes;

    // ── Sound system (flattened) ─────────────────────────────────────────────
    @Column(name = "console_brand", length = 100)
    private String consoleBrand;

    @Column(name = "console_channels")
    private Integer consoleChannels;

    @Column(name = "monitor_mixes")
    private Integer monitorMixes;

    @Column(name = "pa_description", columnDefinition = "TEXT")
    private String paDescription;

    @Column(name = "processor_notes", columnDefinition = "TEXT")
    private String processorNotes;

    // ── Stage dimensions (flattened) ─────────────────────────────────────────
    @Column(name = "stage_width_meters", precision = 6, scale = 2)
    private BigDecimal stageWidthMeters;

    @Column(name = "stage_depth_meters", precision = 6, scale = 2)
    private BigDecimal stageDepthMeters;

    @Column(name = "stage_min_height_meters", precision = 6, scale = 2)
    private BigDecimal stageMinHeightMeters;

    @Column(name = "power_outlets")
    private Integer powerOutlets;

    @Column(name = "has_drum_riser", nullable = false)
    private boolean hasDrumRiser;

    @Column(name = "stage_elements", columnDefinition = "jsonb")
    @JdbcTypeCode(SqlTypes.JSON)
    private String stageElementsJson;

    // ── Hospitality (flattened) ──────────────────────────────────────────────
    @Column(name = "dressing_room_capacity")
    private Integer dressingRoomCapacity;

    @Column(name = "catering_notes", columnDefinition = "TEXT")
    private String cateringNotes;

    @Column(name = "water_bottles_on_stage")
    private Integer waterBottlesOnStage;

    @Column(name = "passes_count")
    private Integer passesCount;

    // ── Transport (flattened) ────────────────────────────────────────────────
    @Column(name = "vehicle_type", length = 100)
    private String vehicleType;

    @Column(name = "passenger_capacity")
    private Integer passengerCapacity;

    @Column(name = "transport_notes", columnDefinition = "TEXT")
    private String transportNotes;

    @Column(name = "additional_notes", columnDefinition = "TEXT")
    private String additionalNotes;

    @Column(name = "share_token", length = 36, unique = true)
    private String shareToken;

    // ── Collections ──────────────────────────────────────────────────────────

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "rider_staff", joinColumns = @JoinColumn(name = "rider_id"))
    @Builder.Default
    private List<StaffMemberEmbeddable> staff = new ArrayList<>();

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "rider_input_channels", joinColumns = @JoinColumn(name = "rider_id"))
    @Builder.Default
    private List<InputChannelEmbeddable> inputChannels = new ArrayList<>();

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "rider_backline_items", joinColumns = @JoinColumn(name = "rider_id"))
    @Builder.Default
    private List<BacklineItemEmbeddable> backlineItems = new ArrayList<>();

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

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

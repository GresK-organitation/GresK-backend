package com.gresk.modules.event.infrastructure.persistence;

import com.gresk.modules.event.domain.model.EventStatus;
import com.gresk.shared.domain.MusicGenre;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "events")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventEntity {

    @Id
    private UUID id;

    @Column(nullable = false, length = 255)
    private String title;

    @Column(name = "promoter_id", nullable = false)
    private UUID promoterId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EventStatus status;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private MusicGenre genre;

    // ── Precio original ──────────────────────────────────────────────────────
    @Column(name = "amount", precision = 10, scale = 2)
    private BigDecimal amount;

    @Column(name = "currency", length = 10)
    private String currency;

    // ── Precio con descuento (null si no aplica) ─────────────────────────────
    @Column(name = "discounted_amount", precision = 10, scale = 2)
    private BigDecimal discountedAmount;

    // ── Aforo ────────────────────────────────────────────────────────────────
    @Column(name = "total_capacity")
    private Integer totalCapacity;

    @Column(name = "available_capacity")
    private Integer availableCapacity;

    // ── Fechas (Instant → TIMESTAMPTZ) ───────────────────────────────────────
    @Column(name = "event_date")
    private Instant eventDate;

    @Column(name = "reveal_at")
    private Instant revealAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    // ── Ubicación ────────────────────────────────────────────────────────────
    @Column(name = "street", length = 255)
    private String street;

    @Column(name = "city", length = 100)
    private String city;

    @Column(name = "country", length = 100)
    private String country;

    @Column(name = "venue", length = 255)
    private String venue;

    @Column(name = "latitude")
    private Double latitude;

    @Column(name = "longitude")
    private Double longitude;

    // ── Imagen de portada ────────────────────────────────────────────────────
    @Column(name = "cover_image_url", length = 512)
    private String coverImageUrl;

    // ── Artista ──────────────────────────────────────────────────────────────
    @Column(name = "artist_name", length = 255)
    private String artistName;

    @Column(name = "artist_image_url", length = 512)
    private String artistImageUrl;

    // ── Rating stats (denormalized community averages) ───────────────────────
    @Column(name = "review_count")
    private Integer reviewCount;

    @Column(name = "avg_overall_rating")
    private Double avgOverallRating;

    @Column(name = "avg_artist_rating")
    private Double avgArtistRating;

    @Column(name = "avg_sound_rating")
    private Double avgSoundRating;

    @Column(name = "avg_ambience_rating")
    private Double avgAmbienceRating;

    @Column(name = "avg_venue_rating")
    private Double avgVenueRating;

    @Column(name = "avg_setlist_rating")
    private Double avgSetlistRating;
}

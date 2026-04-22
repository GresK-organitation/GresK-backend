package com.gresk.modules.event.domain.model;

import com.gresk.modules.event.domain.exception.IncompleteEventException;
import com.gresk.modules.event.domain.exception.InvalidEventTransitionException;
import com.gresk.modules.promoter.domain.model.valueobject.PromoterId;
import com.gresk.shared.domain.MusicGenre;
import com.gresk.shared.domain.valueobject.AssetId;
import com.gresk.shared.domain.valueobject.Percentage;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public final class Event {

    private final EventId      id;
    private final String       title;
    private final PromoterId   promoterId;
    private final Instant      createdAt;

    private EventStatus      status;
    private MusicGenre       genre;
    private Price            price;
    private Price            discountedPrice;   // null mientras no se aplique descuento
    private Capacity         capacity;
    private Instant          eventDate;
    private Location         location;
    private Instant          revealAt;
    private AssetId          coverImage;
    private UUID             artistId;          // FK al agregado Artist (nullable)
    private EventRatingStats ratingStats;

    private Event(EventId id, String title, PromoterId promoterId,
                  MusicGenre genre, Price price, Price discountedPrice,
                  Capacity capacity, Instant eventDate,
                  Location location, Instant revealAt,
                  AssetId coverImage, UUID artistId,
                  EventStatus status, Instant createdAt,
                  EventRatingStats ratingStats) {
        this.id              = id;
        this.title           = title;
        this.promoterId      = promoterId;
        this.genre           = genre;
        this.price           = price;
        this.discountedPrice = discountedPrice;
        this.capacity        = capacity;
        this.eventDate       = eventDate;
        this.location        = location;
        this.revealAt        = revealAt;
        this.coverImage      = coverImage;
        this.artistId        = artistId;
        this.status          = status;
        this.createdAt       = createdAt;
        this.ratingStats     = ratingStats != null ? ratingStats : EventRatingStats.empty();
    }

    // ── Factorías ────────────────────────────────────────────────────────────

    public static Event create(String title, PromoterId promoterId) {
        return new Event(
                EventId.generate(), title, promoterId,
                null, null, null, null, null, null, null, null, null,
                EventStatus.DRAFT, Instant.now(), EventRatingStats.empty()
        );
    }

    /** Reconstitución completa incluyendo stats de valoración. */
    public static Event reconstitute(EventId id, String title, PromoterId promoterId,
                                     MusicGenre genre, Price price, Price discountedPrice,
                                     Capacity capacity, Instant eventDate,
                                     Location location, Instant revealAt,
                                     AssetId coverImage, UUID artistId,
                                     EventStatus status, Instant createdAt,
                                     EventRatingStats ratingStats) {
        return new Event(id, title, promoterId, genre, price, discountedPrice,
                capacity, eventDate, location, revealAt,
                coverImage, artistId, status, createdAt, ratingStats);
    }

    // ── Comportamientos ──────────────────────────────────────────────────────

    public void publish() {
        if (genre == null) {
            throw new IncompleteEventException("Cannot publish event: genre is missing");
        }
        if (price == null) {
            throw new IncompleteEventException("Cannot publish event: price is missing");
        }
        if (capacity == null) {
            throw new IncompleteEventException("Cannot publish event: capacity is missing");
        }
        if (eventDate == null) {
            throw new IncompleteEventException("Cannot publish event: eventDate is missing");
        }
        if (!status.canTransitionTo(EventStatus.PUBLISHED)) {
            throw new InvalidEventTransitionException(
                    "Cannot transition from " + status + " to PUBLISHED");
        }
        this.status = EventStatus.PUBLISHED;
    }

    public void finish() {
        if (!status.canTransitionTo(EventStatus.FINISHED)) {
            throw new InvalidEventTransitionException(
                    "Cannot transition from " + status + " to FINISHED");
        }
        this.status = EventStatus.FINISHED;
    }

    public void cancel() {
        if (!status.canTransitionTo(EventStatus.CANCELLED)) {
            throw new InvalidEventTransitionException(
                    "Cannot transition from " + status + " to CANCELLED");
        }
        this.status = EventStatus.CANCELLED;
    }

    public void decrementCapacity() {
        if (capacity == null) {
            throw new IllegalStateException("Event has no capacity set");
        }
        this.capacity = capacity.reserve(1);
    }

    /**
     * Aplica un descuento al precio original de forma inmutable.
     * El precio original se conserva; discountedPrice refleja el precio con descuento.
     *
     * @param percent porcentaje entre 0 y 100
     */
    public void applyDiscount(Percentage percent) {
        if (price == null) {
            throw new IllegalStateException("Cannot apply discount: event has no price set");
        }
        BigDecimal reduction = price.amount().multiply(percent.asFraction());
        BigDecimal newAmount = price.amount().subtract(reduction);
        this.discountedPrice = new Price(newAmount, price.currency());
    }

    // ── Builder de estado (fluent setters) ───────────────────────────────────

    public Event withGenre(MusicGenre genre) {
        this.genre = genre;
        return this;
    }

    public Event withPrice(Price price) {
        this.price = price;
        return this;
    }

    public Event withCapacity(Capacity capacity) {
        this.capacity = capacity;
        return this;
    }

    public Event withEventDate(Instant eventDate) {
        this.eventDate = eventDate;
        return this;
    }

    public Event withLocation(Location location) {
        this.location = location;
        return this;
    }

    public Event withRevealAt(Instant revealAt) {
        this.revealAt = revealAt;
        return this;
    }

    public Event withCoverImage(AssetId coverImage) {
        this.coverImage = coverImage;
        return this;
    }

    public Event withArtistId(UUID artistId) {
        this.artistId = artistId;
        return this;
    }

    // ── Getters ──────────────────────────────────────────────────────────────

    public EventId    getId()              { return id; }
    public String     getTitle()           { return title; }
    public PromoterId getPromoterId()      { return promoterId; }
    public EventStatus getStatus()         { return status; }
    public Instant    getCreatedAt()       { return createdAt; }
    public MusicGenre getGenre()           { return genre; }
    public Price      getPrice()           { return price; }
    public Price      getDiscountedPrice() { return discountedPrice; }
    public Capacity   getCapacity()        { return capacity; }
    public Instant    getEventDate()       { return eventDate; }
    public Location   getLocation()        { return location; }
    public Instant    getRevealAt()        { return revealAt; }
    public AssetId   getCoverImage()      { return coverImage; }
    public UUID      getArtistId()        { return artistId; }

    /** Precio efectivo: usa discountedPrice si existe, si no el original. */
    public Price effectivePrice() {
        return discountedPrice != null ? discountedPrice : price;
    }

    /** Incorpora una nueva valoración actualizando las medias de la comunidad. */
    public void addRating(int artist, int sound, int ambience,
                          int venue, int setlist, int overall) {
        this.ratingStats = this.ratingStats.withNewRating(
                artist, sound, ambience, venue, setlist, overall);
    }

    /** Reemplaza las stats con un conjunto recalculado (usado tras editar una review). */
    public void replaceRatingStats(int reviewCount,
                                   double avgOverall, double avgArtist,
                                   double avgSound, double avgAmbience,
                                   double avgVenue, double avgSetlist) {
        this.ratingStats = new EventRatingStats(
                reviewCount, avgOverall, avgArtist,
                avgSound, avgAmbience, avgVenue, avgSetlist);
    }

    public EventRatingStats getRatingStats() { return ratingStats; }
}
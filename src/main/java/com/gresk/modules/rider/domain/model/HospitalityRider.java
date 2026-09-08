package com.gresk.modules.rider.domain.model;

import com.gresk.modules.artist.domain.model.valueobject.ArtistId;
import com.gresk.modules.promoter.domain.model.valueobject.PromoterId;
import com.gresk.modules.rider.domain.exception.LineItemNotFoundException;
import com.gresk.modules.rider.domain.exception.RiderAlreadyPublishedException;
import com.gresk.modules.rider.domain.exception.RiderIncompletException;
import com.gresk.modules.rider.domain.model.valueobject.FulfillmentSource;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public final class HospitalityRider {

    private static final Set<RiderItemCategory> ALLOWED_CATEGORIES = Set.of(
            RiderItemCategory.CATERING, RiderItemCategory.DRESSING_ROOM, RiderItemCategory.DIET,
            RiderItemCategory.ACCOMMODATION, RiderItemCategory.TRANSPORT, RiderItemCategory.OTHER);

    private final RiderId id;
    private final ArtistId artistId;
    private final PromoterId promoterId;
    private final Instant createdAt;

    private String name;
    private RiderStatus status;
    private int version;
    private final List<RiderLineItem> lineItems;
    private String additionalNotes;
    private String shareToken;
    private Instant updatedAt;

    private HospitalityRider(RiderId id, ArtistId artistId, PromoterId promoterId, String name, RiderStatus status,
                              int version, List<RiderLineItem> lineItems, String additionalNotes, String shareToken,
                              Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.artistId = artistId;
        this.promoterId = promoterId;
        this.name = name;
        this.status = status;
        this.version = version;
        this.lineItems = lineItems != null ? new ArrayList<>(lineItems) : new ArrayList<>();
        this.additionalNotes = additionalNotes;
        this.shareToken = shareToken;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // ── Factories ────────────────────────────────────────────────────────────

    public static HospitalityRider create(ArtistId artistId, PromoterId promoterId, String name) {
        Instant now = Instant.now();
        return new HospitalityRider(RiderId.generate(), artistId, promoterId, name, RiderStatus.DRAFT, 1,
                List.of(), null, null, now, now);
    }

    public static HospitalityRider reconstitute(
            RiderId id, ArtistId artistId, PromoterId promoterId, String name, RiderStatus status, int version,
            List<RiderLineItem> lineItems, String additionalNotes, String shareToken,
            Instant createdAt, Instant updatedAt) {
        return new HospitalityRider(id, artistId, promoterId, name, status, version, lineItems,
                additionalNotes, shareToken, createdAt, updatedAt);
    }

    // ── Behavior ─────────────────────────────────────────────────────────────

    public void publish() {
        if (status == RiderStatus.PUBLISHED) throw new RiderAlreadyPublishedException();
        if (lineItems.isEmpty()) throw new RiderIncompletException("lineItems");
        this.status = RiderStatus.PUBLISHED;
        touch();
    }

    public void incrementVersion() {
        this.version++;
        touch();
    }

    public String generateShareToken() {
        if (shareToken == null) {
            shareToken = UUID.randomUUID().toString();
            touch();
        }
        return shareToken;
    }

    public RiderLineItem addLineItem(RiderItemCategory category, String description, int quantity,
                                      boolean required, Map<String, String> attributes, String notes) {
        if (!ALLOWED_CATEGORIES.contains(category))
            throw new IllegalArgumentException(category + " is not a valid hospitality rider category");
        RiderLineItem item = RiderLineItem.create(category, description, quantity, required, attributes, notes);
        lineItems.add(item);
        touch();
        return item;
    }

    public void removeLineItem(UUID lineItemId) {
        lineItems.removeIf(i -> i.getId().equals(lineItemId));
        touch();
    }

    public RiderLineItem lineItem(UUID lineItemId) {
        return lineItems.stream().filter(i -> i.getId().equals(lineItemId)).findFirst()
                .orElseThrow(() -> new LineItemNotFoundException(lineItemId));
    }

    public void setLineItemFulfillment(UUID lineItemId, FulfillmentSource source) {
        lineItem(lineItemId).setFulfillment(source);
        touch();
    }

    // ── Fluent setters ───────────────────────────────────────────────────────

    public HospitalityRider withName(String name) {
        this.name = name;
        touch();
        return this;
    }

    public HospitalityRider withAdditionalNotes(String additionalNotes) {
        this.additionalNotes = additionalNotes;
        touch();
        return this;
    }

    private void touch() {
        this.updatedAt = Instant.now();
    }

    // ── Getters ──────────────────────────────────────────────────────────────

    public RiderId getId() { return id; }
    public ArtistId getArtistId() { return artistId; }
    public PromoterId getPromoterId() { return promoterId; }
    public String getName() { return name; }
    public RiderStatus getStatus() { return status; }
    public int getVersion() { return version; }
    public List<RiderLineItem> getLineItems() { return List.copyOf(lineItems); }
    public String getAdditionalNotes() { return additionalNotes; }
    public String getShareToken() { return shareToken; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
}

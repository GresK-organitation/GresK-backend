package com.gresk.modules.rider.domain.model;

import com.gresk.modules.artist.domain.model.valueobject.ArtistId;
import com.gresk.modules.promoter.domain.model.valueobject.PromoterId;
import com.gresk.modules.rider.domain.exception.LineItemNotFoundException;
import com.gresk.modules.rider.domain.exception.RiderAlreadyPublishedException;
import com.gresk.modules.rider.domain.exception.RiderIncompletException;
import com.gresk.modules.rider.domain.model.valueobject.EquipmentEquivalence;
import com.gresk.modules.rider.domain.model.valueobject.FulfillmentSource;
import com.gresk.modules.rider.domain.model.valueobject.StageDimensions;
import com.gresk.modules.rider.domain.model.valueobject.StageElement;
import com.gresk.modules.rider.domain.model.valueobject.StaffMember;
import com.gresk.modules.rider.domain.model.valueobject.SubstitutionStatus;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public final class TechnicalRider {

    private static final Set<RiderItemCategory> ALLOWED_CATEGORIES = Set.of(
            RiderItemCategory.SOUND_PA, RiderItemCategory.MICROPHONE, RiderItemCategory.BACKLINE,
            RiderItemCategory.LIGHTING, RiderItemCategory.STAGE, RiderItemCategory.OTHER);

    private final RiderId id;
    private final ArtistId artistId;
    private final PromoterId promoterId;
    private final Instant createdAt;

    private String name;
    private RiderStatus status;
    private int version;
    private List<StaffMember> staff;
    private Integer soundCheckDurationMinutes;
    private String soundCheckNotes;
    private StageDimensions stageDimensions;
    private List<StageElement> stageElements;
    private final List<RiderLineItem> lineItems;
    private String additionalNotes;
    private String shareToken;
    private Instant updatedAt;

    private TechnicalRider(RiderId id, ArtistId artistId, PromoterId promoterId, String name, RiderStatus status,
                            int version, List<StaffMember> staff, Integer soundCheckDurationMinutes,
                            String soundCheckNotes, StageDimensions stageDimensions, List<StageElement> stageElements,
                            List<RiderLineItem> lineItems, String additionalNotes, String shareToken,
                            Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.artistId = artistId;
        this.promoterId = promoterId;
        this.name = name;
        this.status = status;
        this.version = version;
        this.staff = staff != null ? new ArrayList<>(staff) : new ArrayList<>();
        this.soundCheckDurationMinutes = soundCheckDurationMinutes;
        this.soundCheckNotes = soundCheckNotes;
        this.stageDimensions = stageDimensions;
        this.stageElements = stageElements != null ? new ArrayList<>(stageElements) : new ArrayList<>();
        this.lineItems = lineItems != null ? new ArrayList<>(lineItems) : new ArrayList<>();
        this.additionalNotes = additionalNotes;
        this.shareToken = shareToken;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // ── Factories ────────────────────────────────────────────────────────────

    public static TechnicalRider create(ArtistId artistId, PromoterId promoterId, String name) {
        Instant now = Instant.now();
        return new TechnicalRider(RiderId.generate(), artistId, promoterId, name, RiderStatus.DRAFT, 1,
                List.of(), null, null, null, List.of(), List.of(), null, null, now, now);
    }

    public static TechnicalRider reconstitute(
            RiderId id, ArtistId artistId, PromoterId promoterId, String name, RiderStatus status, int version,
            List<StaffMember> staff, Integer soundCheckDurationMinutes, String soundCheckNotes,
            StageDimensions stageDimensions, List<StageElement> stageElements, List<RiderLineItem> lineItems,
            String additionalNotes, String shareToken, Instant createdAt, Instant updatedAt) {
        return new TechnicalRider(id, artistId, promoterId, name, status, version, staff,
                soundCheckDurationMinutes, soundCheckNotes, stageDimensions, stageElements, lineItems,
                additionalNotes, shareToken, createdAt, updatedAt);
    }

    // ── Behavior ─────────────────────────────────────────────────────────────

    public void publish() {
        if (status == RiderStatus.PUBLISHED) throw new RiderAlreadyPublishedException();
        if (stageDimensions == null) throw new RiderIncompletException("stageDimensions");
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
            throw new IllegalArgumentException(category + " is not a valid technical rider category");
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

    public void proposeLineItemEquivalence(UUID lineItemId, String proposedAlternative,
                                            EquipmentEquivalence.ProposedBy proposedBy, String notes) {
        lineItem(lineItemId).proposeEquivalence(proposedAlternative, proposedBy, notes);
        touch();
    }

    public void decideLineItemEquivalence(UUID lineItemId, SubstitutionStatus decision) {
        lineItem(lineItemId).decideEquivalence(decision);
        touch();
    }

    // ── Fluent setters ───────────────────────────────────────────────────────

    public TechnicalRider withName(String name) {
        this.name = name;
        touch();
        return this;
    }

    public TechnicalRider withStaff(List<StaffMember> staff) {
        this.staff = new ArrayList<>(staff);
        touch();
        return this;
    }

    public TechnicalRider withSoundCheck(Integer duration, String notes) {
        this.soundCheckDurationMinutes = duration;
        this.soundCheckNotes = notes;
        touch();
        return this;
    }

    public TechnicalRider withStageDimensions(StageDimensions stageDimensions) {
        this.stageDimensions = stageDimensions;
        touch();
        return this;
    }

    public TechnicalRider withStageElements(List<StageElement> stageElements) {
        this.stageElements = new ArrayList<>(stageElements);
        touch();
        return this;
    }

    public TechnicalRider withAdditionalNotes(String additionalNotes) {
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
    public List<StaffMember> getStaff() { return List.copyOf(staff); }
    public Integer getSoundCheckDurationMinutes() { return soundCheckDurationMinutes; }
    public String getSoundCheckNotes() { return soundCheckNotes; }
    public StageDimensions getStageDimensions() { return stageDimensions; }
    public List<StageElement> getStageElements() { return List.copyOf(stageElements); }
    public List<RiderLineItem> getLineItems() { return List.copyOf(lineItems); }
    public String getAdditionalNotes() { return additionalNotes; }
    public String getShareToken() { return shareToken; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
}

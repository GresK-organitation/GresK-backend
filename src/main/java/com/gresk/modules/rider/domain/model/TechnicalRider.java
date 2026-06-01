package com.gresk.modules.rider.domain.model;

import com.gresk.modules.artist.domain.model.valueobject.ArtistId;
import com.gresk.modules.promoter.domain.model.valueobject.PromoterId;
import com.gresk.modules.rider.domain.exception.RiderAlreadyPublishedException;
import com.gresk.modules.rider.domain.exception.RiderIncompletException;
import com.gresk.modules.rider.domain.model.valueobject.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public final class TechnicalRider {

    private final RiderId   id;
    private final ArtistId  artistId;
    private final PromoterId promoterId;
    private final Instant   createdAt;

    private String                   name;
    private RiderStatus              status;
    private int                      version;
    private List<StaffMember>        staff;
    private Integer                  soundCheckDurationMinutes;
    private String                   soundCheckNotes;
    private List<InputChannel>       inputChannels;
    private SoundSystemRequirements  soundSystem;
    private List<BacklineItem>       backlineItems;
    private StageDimensions          stageDimensions;
    private List<StageElement>       stageElements;
    private HospitalityRequirements  hospitality;
    private TransportRequirements    transport;
    private String                   additionalNotes;
    private String                   shareToken;
    private Instant                  updatedAt;

    private TechnicalRider(RiderId id, ArtistId artistId, PromoterId promoterId,
                           String name, RiderStatus status, int version,
                           List<StaffMember> staff, Integer soundCheckDurationMinutes,
                           String soundCheckNotes, List<InputChannel> inputChannels,
                           SoundSystemRequirements soundSystem, List<BacklineItem> backlineItems,
                           StageDimensions stageDimensions, List<StageElement> stageElements,
                           HospitalityRequirements hospitality, TransportRequirements transport,
                           String additionalNotes, String shareToken,
                           Instant createdAt, Instant updatedAt) {
        this.id                       = id;
        this.artistId                 = artistId;
        this.promoterId               = promoterId;
        this.name                     = name;
        this.status                   = status;
        this.version                  = version;
        this.staff                    = staff != null ? new ArrayList<>(staff) : new ArrayList<>();
        this.soundCheckDurationMinutes = soundCheckDurationMinutes;
        this.soundCheckNotes          = soundCheckNotes;
        this.inputChannels            = inputChannels != null ? new ArrayList<>(inputChannels) : new ArrayList<>();
        this.soundSystem              = soundSystem;
        this.backlineItems            = backlineItems != null ? new ArrayList<>(backlineItems) : new ArrayList<>();
        this.stageDimensions          = stageDimensions;
        this.stageElements            = stageElements != null ? new ArrayList<>(stageElements) : new ArrayList<>();
        this.hospitality              = hospitality;
        this.transport                = transport;
        this.additionalNotes          = additionalNotes;
        this.shareToken               = shareToken;
        this.createdAt                = createdAt;
        this.updatedAt                = updatedAt;
    }

    // ── Factories ────────────────────────────────────────────────────────────

    public static TechnicalRider create(ArtistId artistId, PromoterId promoterId, String name) {
        Instant now = Instant.now();
        return new TechnicalRider(
                RiderId.generate(), artistId, promoterId,
                name, RiderStatus.DRAFT, 1,
                List.of(), null, null, List.of(), null, List.of(), null, List.of(), null, null,
                null, null, now, now
        );
    }

    public static TechnicalRider reconstitute(
            RiderId id, ArtistId artistId, PromoterId promoterId,
            String name, RiderStatus status, int version,
            List<StaffMember> staff, Integer soundCheckDurationMinutes,
            String soundCheckNotes, List<InputChannel> inputChannels,
            SoundSystemRequirements soundSystem, List<BacklineItem> backlineItems,
            StageDimensions stageDimensions, List<StageElement> stageElements,
            HospitalityRequirements hospitality, TransportRequirements transport,
            String additionalNotes, String shareToken,
            Instant createdAt, Instant updatedAt) {
        return new TechnicalRider(id, artistId, promoterId, name, status, version,
                staff, soundCheckDurationMinutes, soundCheckNotes, inputChannels,
                soundSystem, backlineItems, stageDimensions, stageElements,
                hospitality, transport, additionalNotes, shareToken, createdAt, updatedAt);
    }

    // ── Behavior ─────────────────────────────────────────────────────────────

    public void publish() {
        if (status == RiderStatus.PUBLISHED) throw new RiderAlreadyPublishedException();
        if (soundSystem == null)             throw new RiderIncompletException("soundSystem");
        if (stageDimensions == null)         throw new RiderIncompletException("stageDimensions");
        if (backlineItems.isEmpty())         throw new RiderIncompletException("backlineItems");
        this.status = RiderStatus.PUBLISHED;
        this.updatedAt = Instant.now();
    }

    public void incrementVersion() {
        this.version++;
        this.updatedAt = Instant.now();
    }

    public String generateShareToken() {
        if (shareToken == null) {
            shareToken = UUID.randomUUID().toString();
            updatedAt = Instant.now();
        }
        return shareToken;
    }

    // ── Fluent setters ───────────────────────────────────────────────────────

    public TechnicalRider withName(String name) {
        this.name = name;
        this.updatedAt = Instant.now();
        return this;
    }

    public TechnicalRider withStaff(List<StaffMember> staff) {
        this.staff = new ArrayList<>(staff);
        this.updatedAt = Instant.now();
        return this;
    }

    public TechnicalRider withSoundCheck(Integer duration, String notes) {
        this.soundCheckDurationMinutes = duration;
        this.soundCheckNotes = notes;
        this.updatedAt = Instant.now();
        return this;
    }

    public TechnicalRider withInputChannels(List<InputChannel> inputChannels) {
        this.inputChannels = new ArrayList<>(inputChannels);
        this.updatedAt = Instant.now();
        return this;
    }

    public TechnicalRider withSoundSystem(SoundSystemRequirements soundSystem) {
        this.soundSystem = soundSystem;
        this.updatedAt = Instant.now();
        return this;
    }

    public TechnicalRider withBacklineItems(List<BacklineItem> backlineItems) {
        this.backlineItems = new ArrayList<>(backlineItems);
        this.updatedAt = Instant.now();
        return this;
    }

    public TechnicalRider withStageDimensions(StageDimensions stageDimensions) {
        this.stageDimensions = stageDimensions;
        this.updatedAt = Instant.now();
        return this;
    }

    public TechnicalRider withStageElements(List<StageElement> stageElements) {
        this.stageElements = new ArrayList<>(stageElements);
        this.updatedAt = Instant.now();
        return this;
    }

    public TechnicalRider withHospitality(HospitalityRequirements hospitality) {
        this.hospitality = hospitality;
        this.updatedAt = Instant.now();
        return this;
    }

    public TechnicalRider withTransport(TransportRequirements transport) {
        this.transport = transport;
        this.updatedAt = Instant.now();
        return this;
    }

    public TechnicalRider withAdditionalNotes(String additionalNotes) {
        this.additionalNotes = additionalNotes;
        this.updatedAt = Instant.now();
        return this;
    }

    // ── Getters ──────────────────────────────────────────────────────────────

    public RiderId                  getId()                       { return id; }
    public ArtistId                 getArtistId()                 { return artistId; }
    public PromoterId               getPromoterId()               { return promoterId; }
    public String                   getName()                     { return name; }
    public RiderStatus              getStatus()                   { return status; }
    public int                      getVersion()                  { return version; }
    public List<StaffMember>        getStaff()                    { return List.copyOf(staff); }
    public Integer                  getSoundCheckDurationMinutes(){ return soundCheckDurationMinutes; }
    public String                   getSoundCheckNotes()          { return soundCheckNotes; }
    public List<InputChannel>       getInputChannels()            { return List.copyOf(inputChannels); }
    public SoundSystemRequirements  getSoundSystem()              { return soundSystem; }
    public List<BacklineItem>       getBacklineItems()            { return List.copyOf(backlineItems); }
    public StageDimensions          getStageDimensions()          { return stageDimensions; }
    public List<StageElement>       getStageElements()            { return List.copyOf(stageElements); }
    public HospitalityRequirements  getHospitality()              { return hospitality; }
    public TransportRequirements    getTransport()                { return transport; }
    public String                   getAdditionalNotes()          { return additionalNotes; }
    public String                   getShareToken()               { return shareToken; }
    public Instant                  getCreatedAt()                { return createdAt; }
    public Instant                  getUpdatedAt()                { return updatedAt; }
}

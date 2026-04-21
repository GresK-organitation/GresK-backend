package com.gresk.modules.artist.domain.model;

import com.gresk.modules.artist.domain.model.valueobject.ArtistContact;
import com.gresk.modules.artist.domain.model.valueobject.ArtistFee;
import com.gresk.modules.artist.domain.model.valueobject.ArtistId;
import com.gresk.modules.artist.domain.model.valueobject.ArtistStatus;
import com.gresk.modules.artist.domain.model.valueobject.FollowerCount;
import com.gresk.modules.artist.domain.model.valueobject.SocialLinks;
import com.gresk.modules.promoter.domain.model.valueobject.PromoterId;
import com.gresk.shared.domain.MusicGenre;
import com.gresk.shared.domain.valueobject.City;
import com.gresk.shared.domain.valueobject.Description;
import com.gresk.shared.domain.valueobject.AssetId;
import com.gresk.shared.domain.valueobject.Name;

import java.time.Instant;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

public final class Artist {

    private final ArtistId       id;
    private final PromoterId     promoterId;
    private Name                 name;
    private City                 origin;
    private Set<MusicGenre>      genres;
    private AssetId              imageAssetId;
    private Description          bio;
    private ArtistStatus         status;
    private ArtistFee            fee;
    private FollowerCount        followers;
    private Set<String>          tags;
    private ArtistContact        contact;
    private SocialLinks          socialLinks;
    private int                  eventsPlayed;
    private double               avgRating;
    private final Instant        createdAt;

    private Artist(ArtistId id, PromoterId promoterId, Name name, City origin,
                   Set<MusicGenre> genres, AssetId imageAssetId, Description bio,
                   ArtistStatus status, ArtistFee fee, FollowerCount followers,
                   Set<String> tags, ArtistContact contact, SocialLinks socialLinks,
                   int eventsPlayed, double avgRating, Instant createdAt) {
        this.id           = Objects.requireNonNull(id,         "ArtistId is required");
        this.promoterId   = Objects.requireNonNull(promoterId, "PromoterId is required");
        this.name         = Objects.requireNonNull(name,       "Name is required");
        this.origin       = Objects.requireNonNull(origin,     "Origin is required");
        this.bio          = Objects.requireNonNull(bio,        "Bio is required");
        this.status       = Objects.requireNonNull(status,     "Status is required");
        this.contact      = Objects.requireNonNull(contact,    "Contact is required");
        this.createdAt    = Objects.requireNonNull(createdAt,  "CreatedAt is required");
        this.genres       = genres      != null ? new LinkedHashSet<>(genres)    : new LinkedHashSet<>();
        this.imageAssetId = imageAssetId != null ? imageAssetId : new AssetId("");
        this.fee          = fee         != null ? fee         : ArtistFee.empty();
        this.followers    = followers   != null ? followers   : FollowerCount.empty();
        this.tags         = tags        != null ? new LinkedHashSet<>(tags)      : new LinkedHashSet<>();
        this.socialLinks  = socialLinks != null ? socialLinks : SocialLinks.empty();
        this.eventsPlayed = eventsPlayed;
        this.avgRating    = avgRating;
    }

    // ── Factoría: creación por la promotora ──────────────────────────────────

    public static Artist create(PromoterId promoterId, Name name, City origin,
                                Set<MusicGenre> genres, AssetId imageAssetId, Description bio,
                                ArtistStatus status, ArtistFee fee, FollowerCount followers,
                                Set<String> tags, ArtistContact contact, SocialLinks socialLinks) {
        return new Artist(
                ArtistId.generate(), promoterId, name, origin, genres, imageAssetId, bio,
                status, fee, followers, tags, contact, socialLinks,
                0, 0.0, Instant.now()
        );
    }

    // ── Factoría: reconstitución desde persistencia ──────────────────────────

    public static Artist reconstitute(ArtistId id, PromoterId promoterId, Name name, City origin,
                                      Set<MusicGenre> genres, AssetId imageAssetId, Description bio,
                                      ArtistStatus status, ArtistFee fee, FollowerCount followers,
                                      Set<String> tags, ArtistContact contact, SocialLinks socialLinks,
                                      int eventsPlayed, double avgRating, Instant createdAt) {
        return new Artist(id, promoterId, name, origin, genres, imageAssetId, bio,
                status, fee, followers, tags, contact, socialLinks,
                eventsPlayed, avgRating, createdAt);
    }

    // ── Comportamientos ──────────────────────────────────────────────────────

    public void updateProfile(Name name, City origin, Set<MusicGenre> genres,
                               AssetId imageAssetId, Description bio) {
        this.name         = Objects.requireNonNull(name);
        this.origin       = Objects.requireNonNull(origin);
        this.genres       = genres != null ? new LinkedHashSet<>(genres) : new LinkedHashSet<>();
        this.imageAssetId = imageAssetId != null ? imageAssetId : new AssetId("");
        this.bio          = Objects.requireNonNull(bio);
    }

    public void updateProfessionalInfo(ArtistStatus status, ArtistFee fee,
                                       FollowerCount followers, Set<String> tags) {
        this.status    = Objects.requireNonNull(status);
        this.fee       = fee       != null ? fee       : ArtistFee.empty();
        this.followers = followers != null ? followers : FollowerCount.empty();
        this.tags      = tags      != null ? new LinkedHashSet<>(tags) : new LinkedHashSet<>();
    }

    public void updateContact(ArtistContact contact, SocialLinks socialLinks) {
        this.contact     = Objects.requireNonNull(contact);
        this.socialLinks = socialLinks != null ? socialLinks : SocialLinks.empty();
    }

    public void incrementEventsPlayed() {
        this.eventsPlayed++;
    }

    public void updateAvgRating(double newAvgRating) {
        if (newAvgRating < 0.0 || newAvgRating > 5.0)
            throw new IllegalArgumentException("Rating must be between 0.0 and 5.0");
        this.avgRating = newAvgRating;
    }

    // ── Getters ──────────────────────────────────────────────────────────────

    public ArtistId          getId()           { return id; }
    public PromoterId        getPromoterId()   { return promoterId; }
    public Name              getName()         { return name; }
    public City              getOrigin()       { return origin; }
    public Set<MusicGenre>   getGenres()       { return Collections.unmodifiableSet(genres); }
    public AssetId           getImageAssetId() { return imageAssetId; }
    public Description       getBio()          { return bio; }
    public ArtistStatus      getStatus()       { return status; }
    public ArtistFee         getFee()          { return fee; }
    public FollowerCount     getFollowers()    { return followers; }
    public Set<String>       getTags()         { return Collections.unmodifiableSet(tags); }
    public ArtistContact     getContact()      { return contact; }
    public SocialLinks       getSocialLinks()  { return socialLinks; }
    public int               getEventsPlayed() { return eventsPlayed; }
    public double            getAvgRating()    { return avgRating; }
    public Instant           getCreatedAt()    { return createdAt; }
}

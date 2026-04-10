package com.gresk.modules.artist.domain.model;

import com.gresk.modules.promoter.domain.model.valueobject.PromoterId;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public final class Artist {

    private final ArtistId   id;
    private final PromoterId promoterId;
    private String           name;
    private String           origin;
    private List<String>     genres;
    private String           imageUrl;
    private String           bio;
    private ArtistStatus     status;
    private String           fee;
    private String           followers;
    private String           contact;
    private String           socialSpotify;
    private String           socialInstagram;
    private List<String>     tags;
    private int              eventsPlayed;
    private final Instant    createdAt;

    private Artist(ArtistId id, PromoterId promoterId, String name, String origin,
                   List<String> genres, String imageUrl, String bio, ArtistStatus status,
                   String fee, String followers, String contact, String socialSpotify,
                   String socialInstagram, List<String> tags, int eventsPlayed, Instant createdAt) {
        this.id              = Objects.requireNonNull(id);
        this.promoterId      = Objects.requireNonNull(promoterId);
        this.name            = Objects.requireNonNull(name);
        this.origin          = origin;
        this.genres          = genres != null ? new ArrayList<>(genres) : new ArrayList<>();
        this.imageUrl        = imageUrl;
        this.bio             = Objects.requireNonNull(bio);
        this.status          = Objects.requireNonNull(status);
        this.fee             = fee;
        this.followers       = followers;
        this.contact         = Objects.requireNonNull(contact);
        this.socialSpotify   = socialSpotify;
        this.socialInstagram = socialInstagram;
        this.tags            = tags != null ? new ArrayList<>(tags) : new ArrayList<>();
        this.eventsPlayed    = eventsPlayed;
        this.createdAt       = Objects.requireNonNull(createdAt);
    }

    public static Artist create(ArtistId id, PromoterId promoterId, String name, String origin,
                                List<String> genres, String imageUrl, String bio, ArtistStatus status,
                                String fee, String followers, String contact,
                                String socialSpotify, String socialInstagram, List<String> tags) {
        return new Artist(id, promoterId, name, origin, genres, imageUrl, bio, status,
                fee, followers, contact, socialSpotify, socialInstagram, tags, 0, Instant.now());
    }

    public static Artist reconstitute(ArtistId id, PromoterId promoterId, String name, String origin,
                                      List<String> genres, String imageUrl, String bio, ArtistStatus status,
                                      String fee, String followers, String contact, String socialSpotify,
                                      String socialInstagram, List<String> tags, int eventsPlayed, Instant createdAt) {
        return new Artist(id, promoterId, name, origin, genres, imageUrl, bio, status,
                fee, followers, contact, socialSpotify, socialInstagram, tags, eventsPlayed, createdAt);
    }

    public void update(String name, String origin, List<String> genres, String imageUrl, String bio,
                       ArtistStatus status, String fee, String followers, String contact,
                       String socialSpotify, String socialInstagram, List<String> tags) {
        this.name            = Objects.requireNonNull(name);
        this.origin          = origin;
        this.genres          = genres != null ? new ArrayList<>(genres) : new ArrayList<>();
        this.imageUrl        = imageUrl;
        this.bio             = Objects.requireNonNull(bio);
        this.status          = Objects.requireNonNull(status);
        this.fee             = fee;
        this.followers       = followers;
        this.contact         = Objects.requireNonNull(contact);
        this.socialSpotify   = socialSpotify;
        this.socialInstagram = socialInstagram;
        this.tags            = tags != null ? new ArrayList<>(tags) : new ArrayList<>();
    }

    public boolean belongsTo(PromoterId promoterId) {
        return this.promoterId.equals(promoterId);
    }

    public ArtistId   getId()              { return id; }
    public PromoterId getPromoterId()      { return promoterId; }
    public String     getName()            { return name; }
    public String     getOrigin()          { return origin; }
    public List<String> getGenres()        { return Collections.unmodifiableList(genres); }
    public String     getImageUrl()        { return imageUrl; }
    public String     getBio()             { return bio; }
    public ArtistStatus getStatus()        { return status; }
    public String     getFee()             { return fee; }
    public String     getFollowers()       { return followers; }
    public String     getContact()         { return contact; }
    public String     getSocialSpotify()   { return socialSpotify; }
    public String     getSocialInstagram() { return socialInstagram; }
    public List<String> getTags()          { return Collections.unmodifiableList(tags); }
    public int        getEventsPlayed()    { return eventsPlayed; }
    public Instant    getCreatedAt()       { return createdAt; }
}

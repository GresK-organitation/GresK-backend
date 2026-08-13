package com.gresk.modules.journal.domain.model;

import com.gresk.modules.artist.domain.model.valueobject.ArtistId;
import com.gresk.modules.event.domain.model.EventId;
import com.gresk.modules.journal.domain.exception.InvalidJournalEntryException;
import com.gresk.modules.journal.domain.exception.TooManyMediaItemsException;
import com.gresk.modules.user.domain.model.UserId;
import com.gresk.shared.domain.MusicGenre;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * A free-form music diary entry: any experience the user wants to log
 * (an old festival, a record, an undocumented gig) with no ticket or catalog
 * event required. Unlike {@code review.Review}, entries here never feed the
 * official public rating averages of Event/Artist — optional catalog links
 * are informational/display-only.
 */
public final class JournalEntry {

    private static final int MAX_NOTES_LENGTH = 1000;
    private static final int MAX_CRITERIA     = 10;
    private static final int MAX_PHOTOS       = 10;
    private static final int MAX_VIDEOS       = 5;

    // ── Immutable identity ────────────────────────────────────────────────────
    private final JournalEntryId    id;
    private final UserId            userId;
    private final Instant           createdAt;
    private final JournalEntrySource source;

    // ── Mutable state ─────────────────────────────────────────────────────────
    private String              artistNameFree; // nullable if artistId present
    private ArtistId            artistId;       // nullable, display-only catalog link
    private ApproxDate          date;
    private String              venueName;      // free text, nullable
    private String              city;           // free text, nullable
    private EventId             eventId;        // nullable, display-only catalog link
    private String              notes;          // nullable, max 1000
    private List<RatingCriterion> criteria;
    private List<JournalMedia>  media;
    private MusicGenre          genre;          // nullable
    private JournalVisibility   visibility;
    private Instant             updatedAt;

    private JournalEntry(JournalEntryId id, UserId userId, Instant createdAt, JournalEntrySource source,
                          String artistNameFree, ArtistId artistId, ApproxDate date,
                          String venueName, String city, EventId eventId, String notes,
                          List<RatingCriterion> criteria, List<JournalMedia> media,
                          MusicGenre genre, JournalVisibility visibility, Instant updatedAt) {
        this.id         = Objects.requireNonNull(id, "JournalEntryId is required");
        this.userId     = Objects.requireNonNull(userId, "UserId is required");
        this.createdAt  = Objects.requireNonNull(createdAt, "CreatedAt is required");
        this.source     = Objects.requireNonNull(source, "JournalEntrySource is required");
        this.date       = Objects.requireNonNull(date, "ApproxDate is required");
        this.visibility = Objects.requireNonNull(visibility, "JournalVisibility is required");
        this.updatedAt  = Objects.requireNonNull(updatedAt, "UpdatedAt is required");

        validateArtistPresence(artistNameFree, artistId);
        this.artistNameFree = artistNameFree;
        this.artistId        = artistId;
        this.venueName        = venueName;
        this.city            = city;
        this.eventId        = eventId;
        this.notes            = validateNotes(notes);

        List<RatingCriterion> criteriaCopy = new ArrayList<>(Objects.requireNonNull(criteria, "criteria is required"));
        validateCriteria(criteriaCopy);
        this.criteria = criteriaCopy;

        List<JournalMedia> mediaCopy = new ArrayList<>(Objects.requireNonNull(media, "media is required"));
        validateMedia(mediaCopy);
        this.media = mediaCopy;

        this.genre = genre;
    }

    // ── Factory: new entry ────────────────────────────────────────────────────

    public static JournalEntry create(UserId userId, String artistNameFree, ArtistId artistId,
                                       ApproxDate date, String venueName, String city, EventId eventId,
                                       String notes, List<RatingCriterion> criteria, MusicGenre genre,
                                       JournalVisibility visibility, JournalEntrySource source) {
        Instant now = Instant.now();
        return new JournalEntry(JournalEntryId.generate(), userId, now, source,
                artistNameFree, artistId, date, venueName, city, eventId, notes,
                criteria, new ArrayList<>(), genre, visibility, now);
    }

    // ── Factory: reconstitute from persistence ────────────────────────────────

    public static JournalEntry reconstitute(JournalEntryId id, UserId userId, Instant createdAt,
                                             JournalEntrySource source, String artistNameFree, ArtistId artistId,
                                             ApproxDate date, String venueName, String city, EventId eventId,
                                             String notes, List<RatingCriterion> criteria, List<JournalMedia> media,
                                             MusicGenre genre, JournalVisibility visibility, Instant updatedAt) {
        return new JournalEntry(id, userId, createdAt, source, artistNameFree, artistId, date,
                venueName, city, eventId, notes, criteria, media, genre, visibility, updatedAt);
    }

    // ── Behaviours ────────────────────────────────────────────────────────────

    public void update(String artistNameFree, ArtistId artistId, ApproxDate date,
                        String venueName, String city, EventId eventId, String notes,
                        List<RatingCriterion> criteria, MusicGenre genre) {
        validateArtistPresence(artistNameFree, artistId);
        List<RatingCriterion> criteriaCopy = new ArrayList<>(Objects.requireNonNull(criteria, "criteria is required"));
        validateCriteria(criteriaCopy);

        this.artistNameFree = artistNameFree;
        this.artistId        = artistId;
        this.date           = Objects.requireNonNull(date, "ApproxDate is required");
        this.venueName        = venueName;
        this.city            = city;
        this.eventId        = eventId;
        this.notes            = validateNotes(notes);
        this.criteria        = criteriaCopy;
        this.genre            = genre;
        this.updatedAt        = Instant.now();
    }

    public void addMedia(JournalMedia newMedia) {
        Objects.requireNonNull(newMedia, "JournalMedia is required");
        List<JournalMedia> updated = new ArrayList<>(this.media);
        updated.add(newMedia);
        validateMedia(updated);
        this.media = updated;
        this.updatedAt = Instant.now();
    }

    public void removeMedia(JournalMediaId mediaId) {
        this.media.removeIf(m -> m.id().equals(mediaId));
        this.updatedAt = Instant.now();
    }

    public void makePublic() {
        this.visibility = JournalVisibility.PUBLIC;
        this.updatedAt  = Instant.now();
    }

    public void makePrivate() {
        this.visibility = JournalVisibility.PRIVATE;
        this.updatedAt  = Instant.now();
    }

    // ── Validation ────────────────────────────────────────────────────────────

    private static void validateArtistPresence(String artistNameFree, ArtistId artistId) {
        if ((artistNameFree == null || artistNameFree.isBlank()) && artistId == null) {
            throw new InvalidJournalEntryException("Either artistName or artistId is required");
        }
    }

    private static String validateNotes(String notes) {
        if (notes != null && notes.length() > MAX_NOTES_LENGTH) {
            throw new InvalidJournalEntryException("Notes must not exceed " + MAX_NOTES_LENGTH + " characters");
        }
        return (notes == null || notes.isBlank()) ? null : notes.strip();
    }

    private static void validateCriteria(List<RatingCriterion> criteria) {
        if (criteria.size() > MAX_CRITERIA) {
            throw new InvalidJournalEntryException("At most " + MAX_CRITERIA + " rating criteria are allowed");
        }
    }

    private static void validateMedia(List<JournalMedia> media) {
        long photoCount = media.stream().filter(m -> m.mediaType() == JournalMediaType.PHOTO).count();
        long videoCount = media.stream().filter(m -> m.mediaType() == JournalMediaType.VIDEO).count();
        if (photoCount > MAX_PHOTOS) {
            throw new TooManyMediaItemsException("At most " + MAX_PHOTOS + " photos are allowed per entry");
        }
        if (videoCount > MAX_VIDEOS) {
            throw new TooManyMediaItemsException("At most " + MAX_VIDEOS + " video clips are allowed per entry");
        }
    }

    // ── Getters ───────────────────────────────────────────────────────────────

    public JournalEntryId       getId()             { return id; }
    public UserId                getUserId()         { return userId; }
    public Instant               getCreatedAt()       { return createdAt; }
    public JournalEntrySource    getSource()         { return source; }
    public String               getArtistNameFree()  { return artistNameFree; }
    public ArtistId             getArtistId()        { return artistId; }
    public ApproxDate           getDate()            { return date; }
    public String               getVenueName()       { return venueName; }
    public String               getCity()            { return city; }
    public EventId               getEventId()        { return eventId; }
    public String               getNotes()           { return notes; }
    public List<RatingCriterion> getCriteria()       { return Collections.unmodifiableList(criteria); }
    public List<JournalMedia>    getMedia()          { return Collections.unmodifiableList(media); }
    public MusicGenre           getGenre()           { return genre; }
    public JournalVisibility    getVisibility()      { return visibility; }
    public Instant               getUpdatedAt()      { return updatedAt; }
}

package com.gresk.modules.promoter.domain.model;

import com.gresk.modules.promoter.domain.exception.GenreNotFoundException;
import com.gresk.modules.promoter.domain.exception.PromoterAlreadyActiveException;
import com.gresk.modules.promoter.domain.exception.PromoterNotActiveException;
import com.gresk.modules.promoter.domain.model.valueobject.PromoterId;
import com.gresk.shared.domain.AccountStatus;
import com.gresk.shared.domain.MusicGenre;
import com.gresk.shared.domain.valueobject.Address;
import com.gresk.shared.domain.valueobject.AssetId;
import com.gresk.shared.domain.valueobject.Description;
import com.gresk.shared.domain.valueobject.Email;
import com.gresk.shared.domain.valueobject.Name;

import java.time.Instant;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

public final class Promoter {

    private final PromoterId id;
    private Email email;
    private Name name;
    private Description description;
    private Address address;
    private AssetId logoAssetId;
    private Set<MusicGenre> musicalGenres;
    private AccountStatus status;
    private Instant createdAt;

    private Promoter(PromoterId id, AssetId logoAssetId, Email email, Name name, Address address, Description description,
                     Set<MusicGenre> musicalGenres, AccountStatus status, Instant createdAt) {
        this.id = id;
        this.email = Objects.requireNonNull(email);
        this.name = Objects.requireNonNull(name);
        this.address = Objects.requireNonNull(address);
        this.description = Objects.requireNonNull(description);
        this.logoAssetId = logoAssetId != null ? logoAssetId : new AssetId(null);
        this.musicalGenres = musicalGenres != null ? new LinkedHashSet<>(musicalGenres) : new LinkedHashSet<>();
        this.status = Objects.requireNonNull(status);
        this.createdAt = Objects.requireNonNull(createdAt);
    }

    public static Promoter create(PromoterId id, Email email, Name name, Address address, Description description) {
        return new Promoter(id, new AssetId(null), email, name, address,
                Objects.requireNonNull(description), new LinkedHashSet<>(), AccountStatus.PENDING, Instant.now());
    }

    public static Promoter create(PromoterId id, AssetId logoAssetId, Email email, Name name,
                                   Address address, Description description, Set<MusicGenre> musicalGenres) {
        return new Promoter(id, logoAssetId != null ? logoAssetId : new AssetId(null), email, name, address,
                Objects.requireNonNull(description),
                musicalGenres != null ? new LinkedHashSet<>(musicalGenres) : new LinkedHashSet<>(),
                AccountStatus.PENDING, Instant.now());
    }

    public static Promoter reconstitute(PromoterId id, AssetId logoAssetId, Email email, Name name, Address address, Description description,
                                        Set<MusicGenre> musicalGenres, AccountStatus status, Instant createdAt) {
        return new Promoter(id, logoAssetId, email, name, address, description, musicalGenres, status, createdAt);
    }

    public void activate() {
        if (this.status == AccountStatus.ACTIVE)
            throw new PromoterAlreadyActiveException("Promoter is already active: " + id);
        this.status = AccountStatus.ACTIVE;
    }

    public void suspend() {
        if (this.status != AccountStatus.ACTIVE)
            throw new PromoterNotActiveException(
                    "Only active Promoters can be suspended. Status: " + status);
        this.status = AccountStatus.SUSPENDED;
    }

    public void addGenre(MusicGenre genre) {
        if (this.status != AccountStatus.ACTIVE) {
            throw new PromoterNotActiveException("Genres can only be added to active promoters");
        }
        if (genre == null) throw new IllegalArgumentException("Genre can't be null");
        musicalGenres.add(genre);
    }

    public void deleteGenre(MusicGenre genre) {
        if (this.status != AccountStatus.ACTIVE) {
            throw new PromoterNotActiveException("Genres can only be deleted from active promoters");
        }
        if (!musicalGenres.contains(genre))
            throw new GenreNotFoundException("This genre is not on the list:" + genre);
        musicalGenres.remove(genre);
    }

    public void updateBasicInfo(Name name, Address address, Description description) {
        this.name = name;
        this.address = address;
        this.description = description;
    }

    public void updateLogo(AssetId assetId) {
        this.logoAssetId = assetId != null ? assetId : new AssetId(null);
    }

    public void replaceGenres(Set<MusicGenre> genres) {
        if (this.status != AccountStatus.ACTIVE) {
            throw new PromoterNotActiveException("Genres can only be updated on active promoters");
        }
        this.musicalGenres = genres != null ? new LinkedHashSet<>(genres) : new LinkedHashSet<>();
    }

    public PromoterId getId() { return id; }
    public Email getEmail() { return email; }
    public Name getName() { return name; }
    public Description getDescription() { return description; }
    public Address getAddress() { return address; }
    public AccountStatus getStatus() { return status; }
    public AssetId getLogoAssetId() { return logoAssetId; }
    public Instant getCreatedAt() { return createdAt; }
    public boolean isActive() { return status == AccountStatus.ACTIVE; }
    public Set<MusicGenre> getMusicalGenres() { return Collections.unmodifiableSet(musicalGenres); }
}

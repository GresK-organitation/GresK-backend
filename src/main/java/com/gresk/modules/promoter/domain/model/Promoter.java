package com.gresk.modules.promoter.domain.model;

import com.gresk.modules.account.domain.model.AccountId;
import com.gresk.modules.promoter.domain.exception.GenreNotFoundException;
import com.gresk.modules.promoter.domain.model.valueobject.PromoterId;
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
    private final AccountId  accountId;
    private Email email;
    private Name name;
    private Description description;
    private Address address;
    private AssetId logoAssetId;
    private Set<MusicGenre> musicalGenres;
    private Instant createdAt;
    private String phone;
    private String website;

    private Promoter(PromoterId id, AccountId accountId, AssetId logoAssetId, Email email, Name name,
                     Address address, Description description, Set<MusicGenre> musicalGenres,
                     Instant createdAt, String phone, String website) {
        this.id          = Objects.requireNonNull(id);
        this.accountId   = Objects.requireNonNull(accountId);
        this.email       = Objects.requireNonNull(email);
        this.name        = Objects.requireNonNull(name);
        this.address     = Objects.requireNonNull(address);
        this.description = Objects.requireNonNull(description);
        this.logoAssetId = logoAssetId != null ? logoAssetId : new AssetId(null);
        this.musicalGenres = musicalGenres != null ? new LinkedHashSet<>(musicalGenres) : new LinkedHashSet<>();
        this.createdAt   = Objects.requireNonNull(createdAt);
        this.phone       = phone;
        this.website     = website;
    }

    public static Promoter create(PromoterId id, AccountId accountId, Email email,
                                   Name name, Address address, Description description) {
        return new Promoter(id, accountId, new AssetId(null), email, name, address,
                Objects.requireNonNull(description), new LinkedHashSet<>(), Instant.now(), null, null);
    }

    public static Promoter create(PromoterId id, AccountId accountId, AssetId logoAssetId,
                                   Email email, Name name, Address address, Description description,
                                   Set<MusicGenre> musicalGenres, String phone, String website) {
        return new Promoter(id, accountId, logoAssetId != null ? logoAssetId : new AssetId(null),
                email, name, address, Objects.requireNonNull(description),
                musicalGenres != null ? new LinkedHashSet<>(musicalGenres) : new LinkedHashSet<>(),
                Instant.now(), phone, website);
    }

    public static Promoter reconstitute(PromoterId id, AccountId accountId, AssetId logoAssetId,
                                        Email email, Name name, Address address, Description description,
                                        Set<MusicGenre> musicalGenres, Instant createdAt,
                                        String phone, String website) {
        return new Promoter(id, accountId, logoAssetId, email, name, address, description,
                musicalGenres, createdAt, phone, website);
    }

    public void addGenre(MusicGenre genre) {
        if (genre == null) throw new IllegalArgumentException("Genre can't be null");
        musicalGenres.add(genre);
    }

    public void deleteGenre(MusicGenre genre) {
        if (!musicalGenres.contains(genre))
            throw new GenreNotFoundException("This genre is not on the list: " + genre);
        musicalGenres.remove(genre);
    }

    public void updateBasicInfo(Name name, Address address, Description description) {
        this.name        = name;
        this.address     = address;
        this.description = description;
    }

    public void updateLogo(AssetId assetId) {
        this.logoAssetId = assetId != null ? assetId : new AssetId(null);
    }

    public void replaceGenres(Set<MusicGenre> genres) {
        this.musicalGenres = genres != null ? new LinkedHashSet<>(genres) : new LinkedHashSet<>();
    }

    public PromoterId getId()                        { return id; }
    public AccountId  getAccountId()                 { return accountId; }
    public Email      getEmail()                     { return email; }
    public Name       getName()                      { return name; }
    public Description getDescription()              { return description; }
    public Address    getAddress()                   { return address; }
    public AssetId    getLogoAssetId()               { return logoAssetId; }
    public Instant    getCreatedAt()                 { return createdAt; }
    public String     getPhone()                     { return phone; }
    public String     getWebsite()                   { return website; }
    public Set<MusicGenre> getMusicalGenres()        { return Collections.unmodifiableSet(musicalGenres); }
}

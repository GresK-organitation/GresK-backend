package com.gresk.modules.user.domain.model;

import com.gresk.modules.account.domain.model.AccountId;
import com.gresk.modules.user.domain.exception.InvalidPointsException;
import com.gresk.shared.domain.MusicGenre;
import com.gresk.shared.domain.Role;
import com.gresk.shared.domain.valueobject.AssetId;
import com.gresk.shared.domain.valueobject.City;
import com.gresk.shared.domain.valueobject.Description;
import com.gresk.shared.domain.valueobject.Email;
import com.gresk.shared.domain.valueobject.Name;

import java.time.Instant;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public final class User {
    private static final int PREMIUM_THRESHOLD = 500;

    private final AccountId accountId;
    private final UserId id;
    private final Email email;
    private final Instant createdAt;

    private Name name;
    private Description description;
    private City city;
    private AssetId avatarAssetId;
    private Set<MusicGenre> musicGenres;
    private UserTier tier;
    private int loyaltyPoints;
    private final Set<Role> roles;

    private User(AccountId accountId, UserId id, Email email, Name name, Description description, City city, AssetId avatarAssetId, Set<MusicGenre> musicGenres, UserTier tier, int loyaltyPoints, Set<Role> roles, Instant createdAt) {
        this.accountId = Objects.requireNonNull(accountId);
        this.id = Objects.requireNonNull(id);
        this.email = Objects.requireNonNull(email);
        this.name = Objects.requireNonNull(name);
        this.description = description;
        this.city = Objects.requireNonNull(city);
        this.avatarAssetId = avatarAssetId != null ? avatarAssetId : new AssetId(null);
        this.musicGenres = musicGenres != null ? new HashSet<>(musicGenres) : new HashSet<>();
        this.tier = Objects.requireNonNull(tier);
        this.loyaltyPoints = loyaltyPoints;
        this.roles = roles != null ? Set.copyOf(roles) : Set.of(Role.USER);
        this.createdAt = Objects.requireNonNull(createdAt);
    }

    public static User create(AccountId accountId, UserId id, Email email, Name name, Description description, City city, Set<MusicGenre> musicGenres) {
        return new User(accountId, id, email, name, description, city, new AssetId(null), musicGenres, UserTier.FREE, 0, Set.of(Role.USER), Instant.now());
    }

    public static User create(AccountId accountId, UserId id, Email email, Name name, Description description, City city, AssetId avatarAssetId, Set<MusicGenre> musicGenres) {
        return new User(accountId, id, email, name, description, city, avatarAssetId, musicGenres, UserTier.FREE, 0, Set.of(Role.USER), Instant.now());
    }

    public static User reconstitute(AccountId accountId, UserId id, Email email, Name name, Description description, City city, AssetId avatarAssetId, Set<MusicGenre> musicGenres, UserTier tier, int loyaltyPoints, Set<Role> roles, Instant createdAt) {
        return new User(accountId, id, email, name, description, city, avatarAssetId, musicGenres, tier, loyaltyPoints, roles, createdAt);
    }

    public void updateProfile(Name name, Description description, City city, Set<MusicGenre> genres) {
        this.name = Objects.requireNonNull(name);
        this.description = description;
        this.city = Objects.requireNonNull(city);
        this.musicGenres = genres != null ? new HashSet<>(genres) : new HashSet<>();
    }

    public void addPoints(int points) {
        if (points <= 0) throw new InvalidPointsException("Points must be positive");
        this.loyaltyPoints += points;
        checkTierUpgrade();
    }

    private void checkTierUpgrade() {
        if (this.loyaltyPoints >= PREMIUM_THRESHOLD && this.tier == UserTier.FREE) {
            this.tier = UserTier.PREMIUM;
        }
    }

    public void updateAvatar(AssetId assetId) {
        this.avatarAssetId = assetId != null ? assetId : new AssetId(null);
    }

    public void changeCity(City newCity) {
        this.city = Objects.requireNonNull(newCity);
    }

    public AccountId getAccountId() {
        return accountId;
    }

    public City getCity() {
        return city;
    }

    public UserId getId() {
        return id;
    }

    public Email getEmail() {
        return email;
    }

    public Name getName() {
        return name;
    }

    public Description getDescription() {
        return description;
    }

    public AssetId getAvatarAssetId() {
        return avatarAssetId;
    }

    public Set<MusicGenre> getMusicGenres() {
        return Set.copyOf(musicGenres);
    }

    public UserTier getTier() {
        return tier;
    }

    public int getLoyaltyPoints() {
        return loyaltyPoints;
    }

    public Set<Role> getRoles() {
        return roles;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}

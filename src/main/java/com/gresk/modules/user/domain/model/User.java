package com.gresk.modules.user.domain.model;

import com.gresk.modules.user.domain.exception.InvalidPointsException;
import com.gresk.shared.domain.AccountStatus;
import com.gresk.shared.domain.MusicGenre;
import com.gresk.shared.domain.Role;
import com.gresk.shared.domain.valueobject.Description;
import com.gresk.shared.domain.valueobject.Email;
import com.gresk.shared.domain.valueobject.Name;
import com.gresk.shared.domain.valueobject.Password;

import java.time.Instant;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public final class User {
    private static final int PREMIUM_THRESHOLD = 500;

    private final UserId id;
    private final Email email;
    private final Password password;
    private final Instant createdAt;

    private Name name;
    private Description description;
    private City city;
    private Set<MusicGenre> musicGenres;
    private AccountStatus status;
    private UserTier tier;
    private int loyaltyPoints;
    private final Set<Role> roles;

    private User(UserId id, Email email, Password password, Name name, Description description, City city, Set<MusicGenre> musicGenres, AccountStatus status, UserTier tier, int loyaltyPoints, Set<Role> roles, Instant createdAt) {
        this.id = Objects.requireNonNull(id);
        this.email = Objects.requireNonNull(email);
        this.password = Objects.requireNonNull(password);
        this.name = Objects.requireNonNull(name);
        this.description = description;
        this.city = Objects.requireNonNull(city);
        this.musicGenres = musicGenres != null ? new HashSet<>(musicGenres) : new HashSet<>();
        this.status = Objects.requireNonNull(status);
        this.tier = Objects.requireNonNull(tier);
        this.loyaltyPoints = loyaltyPoints;
        this.roles = roles != null ? Set.copyOf(roles) : Set.of(Role.USER);
        this.createdAt = Objects.requireNonNull(createdAt);
    }

    public static User create(Email email, Password password, Name name, Description description, City city, Set<MusicGenre> musicGenres) {
        return new User(UserId.generate(), email, password, name, description, city, musicGenres, AccountStatus.ACTIVE, UserTier.FREE, 0, Set.of(Role.USER), Instant.now());
    }

    public static User reconstitute(UserId id, Email email, Password password, Name name, Description description, City city, Set<MusicGenre> musicGenres, AccountStatus status, UserTier tier, int loyaltyPoints, Set<Role> roles, Instant createdAt) {
        return new User(id, email, password, name, description, city, musicGenres, status, tier, loyaltyPoints, roles, createdAt);
    }

    public void updateProfile(Name name, Description description, City city, Set<MusicGenre> genres) {
        this.name = Objects.requireNonNull(name);
        this.description = description;
        this.city = Objects.requireNonNull(city);
        this.musicGenres = genres != null ? new HashSet<>(genres) : new HashSet<>();
    }

    public void addPoints(int points) {
        if (this.status == AccountStatus.SUSPENDED) {
            throw new IllegalStateException("Suspended users cannot earn points");
        }
        if (points <= 0) throw new InvalidPointsException("Points must be positive");
        this.loyaltyPoints += points;
        checkTierUpgrade();
    }

    public void suspendAccount() {
        if (this.status == AccountStatus.DELETED) {
            throw new IllegalStateException("Cannot suspend a deleted user");
        }
        if (this.status == AccountStatus.SUSPENDED) {
            throw new IllegalStateException("User is already suspended");
        }
        this.status = AccountStatus.SUSPENDED;
    }

    public void reactivateAccount() {
        if (this.status != AccountStatus.SUSPENDED) {
            throw new IllegalStateException("Only suspended users can be reactivated");
        }
        this.status = AccountStatus.ACTIVE;
    }

    public void deleteAccount() {
        this.status = AccountStatus.DELETED;
    }
    private void checkTierUpgrade() {
        if (this.loyaltyPoints >= PREMIUM_THRESHOLD && this.tier == UserTier.FREE) {
            this.tier = UserTier.PREMIUM;
        }
    }

    public City getCity() {
        return city;
    }

    public void changeCity(City newCity) {
        this.city = Objects.requireNonNull(newCity);
    }

    public UserId getId() {
        return id;
    }

    public Email getEmail() {
        return email;
    }

    public Password getPassword() {
        return password;
    }

    public Name getName() {
        return name;
    }

    public Description getDescription() {
        return description;
    }

    public Set<MusicGenre> getMusicGenres() {
        return Set.copyOf(musicGenres);
    }

    public AccountStatus getStatus() {
        return status;
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
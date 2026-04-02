package com.gresk.modules.user.domain.model;

import com.gresk.shared.domain.AccountStatus;
import com.gresk.shared.domain.MusicGenre;
import com.gresk.shared.domain.Role;
import com.gresk.shared.domain.valueobject.Description;
import com.gresk.shared.domain.valueobject.Email;
import com.gresk.shared.domain.valueobject.Name;
import com.gresk.shared.domain.valueobject.Password;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public final class User {
    private final UserId id;
    private final Email email;
    private final Password password;
    private final LocalDateTime createdAt;

    private Name name;
    private Description description;
    private List<MusicGenre> musicGenres;
    private AccountStatus status;
    private UserTier tier;
    private int loyaltyPoints;
    private final Set<Role> roles;

    private User(UserId id, Email email, Password password, Name name,
                 Description description, List<MusicGenre> musicGenres,
                 AccountStatus status, UserTier tier, int loyaltyPoints,
                 Set<Role> roles, LocalDateTime createdAt) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.name = name;
        this.description = description;
        this.musicGenres = List.copyOf(musicGenres);
        this.status = status;
        this.tier = tier;
        this.loyaltyPoints = loyaltyPoints;
        this.roles = Set.copyOf(roles);
        this.createdAt = createdAt;
    }

    public static User create(Email email, Password password, Name name,
                              Description description, List<MusicGenre> musicGenres) {
        return new User(
                UserId.generate(), email, password, name, description,
                musicGenres, AccountStatus.ACTIVE, UserTier.FREE, 0,
                Set.of(Role.USER), LocalDateTime.now());
    }


    public void updateProfile(Name name, Description description, List<MusicGenre> genres) {
        this.name = Objects.requireNonNull(name);
        this.description = Objects.requireNonNull(description);
        this.musicGenres = List.copyOf(genres);
    }

    public void addPoints(int points) {
        if (points < 0) throw new IllegalArgumentException("Points cannot be negative");
        this.loyaltyPoints += points;
        checkTierUpgrade();
    }

    private void checkTierUpgrade() {
        if (this.loyaltyPoints >= 500 && this.tier == UserTier.FREE) {
            this.tier = UserTier.PREMIUM;
        }
    }

    public int getLoyaltyPoints() { return loyaltyPoints; }
    public UserId getId() { return id; }
    public Email getEmail() { return email; }
    public Password getPassword() { return password; }
    public Name getName() { return name; }
    public Description getDescription() { return description; }
    public List<MusicGenre> getMusicGenres() { return musicGenres; }
    public AccountStatus getStatus() { return status; }
    public UserTier getTier() { return tier; }
    public Set<Role> getRoles() { return roles; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
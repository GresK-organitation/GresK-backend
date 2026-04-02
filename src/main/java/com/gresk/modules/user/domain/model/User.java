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
import java.util.Set;

public final class User {
    private final UserId id;
    private final Email email;
    private final Password password;
    private final Name name;
    private final Description description;
    private final List<MusicGenre> musicGenres;
    private final AccountStatus status;
    private final UserTier tier;
    private final Set<Role> roles;
    private final LocalDateTime createdAt;

    private User(UserId id,
                 Email email,
                 Password password,
                 Name name,
                 Description description,
                 List<MusicGenre> musicGenres,
                 AccountStatus status,
                 UserTier tier,
                 Set<Role> roles,
                 LocalDateTime createdAt) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.name = name;
        this.description = description;
        this.musicGenres = List.copyOf(musicGenres);
        this.status = status;
        this.tier = tier;
        this.roles = Set.copyOf(roles);
        this.createdAt = createdAt;
    }

    public static User create(Email email,
                          Password password,
                          Name name,
                          Description description,
                          List<MusicGenre> musicGenres) {
        return new User(
                UserId.generate(),
                email,
                password,
                name,
                description,
                musicGenres,
                AccountStatus.ACTIVE,
                UserTier.FREE,
                Set.of(Role.USER),
                LocalDateTime.now());
    }

    public static User reconstitute(UserId userId,
                                    Email email,
                                    Password password,
                                    Name name,
                                    Description description,
                                    List<MusicGenre> musicGenres,
                                    AccountStatus status,
                                    UserTier tier,
                                    Set<Role> roles,
                                    LocalDateTime createdAt) {
        return new User(userId, email, password, name, description, musicGenres, status, tier, roles, createdAt);
    }

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
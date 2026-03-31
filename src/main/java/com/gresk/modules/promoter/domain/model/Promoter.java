package com.gresk.modules.promoter.domain.model;

import com.gresk.modules.promoter.domain.MusicGenre;
import com.gresk.modules.promoter.domain.PromoterStatus;
import com.gresk.modules.promoter.domain.exception.GenreNotFoundException;
import com.gresk.modules.promoter.domain.exception.PromoterAlreadyActiveException;
import com.gresk.modules.promoter.domain.exception.PromoterNotActiveException;
import com.gresk.modules.promoter.domain.valueobject.*;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

public final class Promoter {

    private final PromoterId id;
    private final Email email;
    private final Password password;
    private final PromoterName name;
    private final Description description;
    private final Location location;
    private final Set<MusicGenre> musicalGenres;
    private PromoterStatus status;
    private final LocalDateTime createdAt;
    private boolean active;

    private Promoter(PromoterId id, Email email, Password password, PromoterName name, Description description,
                     Location location, Set<MusicGenre> musicalGenres, PromoterStatus status, LocalDateTime createdAt, boolean active) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.name = name;
        this.description = description;
        this.location = location;
        this.musicalGenres = new LinkedHashSet<>(musicalGenres);
        this.status = status;
        this.createdAt = createdAt;
        this.active = active;
    }

    public static Promoter create(Email email, Password password, PromoterName name, Location location, Description description){
        return new Promoter(
                PromoterId.generate(), email, password, name,
                description != null ? description : new Description(null),
                location,
                new LinkedHashSet<>(),
                PromoterStatus.PENDING,
                LocalDateTime.now(),
                false
        );
    }

    public static Promoter reconstitute(PromoterId id, Email email, Password password,
                                        PromoterName name, Description description,
                                        Location location, Set<MusicGenre> musicalGenres,
                                        PromoterStatus status, LocalDateTime createdAt,
                                        boolean active) {
        return new Promoter(id, email, password, name, description,
                location, musicalGenres, status, createdAt, active);
    }

    public void activate() {
        if (this.status == PromoterStatus.ACTIVE)
            throw new PromoterAlreadyActiveException("Promoter is already active: " + id);
        this.status = PromoterStatus.ACTIVE;
        this.active = true;
    }

    public void suspend() {
        if (this.status != PromoterStatus.ACTIVE)
            throw new PromoterNotActiveException(
                    "Only active Promoters can be suspended. Status: " + status);
        this.status = PromoterStatus.SUSPENDED;
        this.active = false;
    }

    public void addGenre(MusicGenre genre){
        if(genre == null){
            throw new IllegalArgumentException("Genre can't be null");
        }
        musicalGenres.add(genre);
    }

    public void deleteGenre(MusicGenre genre){
        if(!musicalGenres.contains(genre)){
            throw new GenreNotFoundException("This genre is not on the list:" + genre);
        }
        musicalGenres.remove(genre);
    }

    public PromoterId getId(){ return id; }
    public Email getEmail(){ return email; }
    public Password getPassword(){ return password; }
    public PromoterName getName(){ return name; }
    public Description getDescription(){ return description; }
    public Location getLocation(){ return location; }
    public PromoterStatus getStatus(){ return status; }
    public LocalDateTime getCreatedAt(){ return createdAt; }
    public boolean isActive(){ return active; }
    public Set<MusicGenre> getMusicalGenres() {
        return Collections.unmodifiableSet(musicalGenres);
    }


}

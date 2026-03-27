package com.gresk.modules.promoter.domain.model;

import com.gresk.modules.promoter.MusicGenre;
import com.gresk.modules.promoter.PromoterStatus;
import com.gresk.modules.promoter.exception.GenreNotFoundException;
import com.gresk.modules.promoter.exception.PromoterAlreadyActiveException;
import com.gresk.modules.promoter.exception.PromoterNotActiveException;
import com.gresk.modules.promoter.model.Promoter;
import com.gresk.modules.promoter.valueObjects.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Set;

import static org.assertj.core.api.Assertions.*;

class PromoterTest {

    private Email email;
    private Password password;
    private PromoterName name;
    private Location location;

    @BeforeEach
    void setUp() {
        email = new Email("promoter@gresk.com");
        password = new Password("$2a$10$hashedpassword");
        name = new PromoterName("Club Nocturno");
        location = new Location("Madrid", "España", null);
    }

    // --- create() ---

    @Test
    void create_shouldReturnPromoterWithPendingStatusAndInactive() {
        Promoter promoter = Promoter.create(email, password, name, location);

        assertThat(promoter.getStatus()).isEqualTo(PromoterStatus.PENDING);
        assertThat(promoter.isActive()).isFalse();
    }

    @Test
    void create_shouldAssignGeneratedId() {
        Promoter promoter = Promoter.create(email, password, name, location);

        assertThat(promoter.getId()).isNotNull();
    }

    @Test
    void create_shouldAssignEmptyMusicGenres() {
        Promoter promoter = Promoter.create(email, password, name, location);

        assertThat(promoter.getMusicalGenres()).isEmpty();
    }

    @Test
    void create_shouldAssignNullDescription() {
        Promoter promoter = Promoter.create(email, password, name, location);

        assertThat(promoter.getDescription().value()).isNull();
    }

    @Test
    void create_shouldAssignCreatedAtTimestamp() {
        LocalDateTime before = LocalDateTime.now().minusSeconds(1);

        Promoter promoter = Promoter.create(email, password, name, location);

        assertThat(promoter.getCreatedAt()).isAfter(before);
        assertThat(promoter.getCreatedAt()).isBefore(LocalDateTime.now().plusSeconds(1));
    }

    @Test
    void create_shouldPersistAllProvidedFields() {
        Promoter promoter = Promoter.create(email, password, name, location);

        assertThat(promoter.getEmail()).isEqualTo(email);
        assertThat(promoter.getPassword()).isEqualTo(password);
        assertThat(promoter.getName()).isEqualTo(name);
        assertThat(promoter.getLocation()).isEqualTo(location);
    }

    // --- reconstitute() ---

    @Test
    void reconstitute_shouldRestoreAllFields() {
        PromoterId id = PromoterId.generate();
        Description description = new Description("Una descripción");
        Set<MusicGenre> genres = Set.of(MusicGenre.ROCK, MusicGenre.POP);
        LocalDateTime createdAt = LocalDateTime.of(2024, 1, 15, 10, 0);

        Promoter promoter = Promoter.reconstitute(id, email, password, name, description,
                location, genres, PromoterStatus.ACTIVE, createdAt, true);

        assertThat(promoter.getId()).isEqualTo(id);
        assertThat(promoter.getEmail()).isEqualTo(email);
        assertThat(promoter.getPassword()).isEqualTo(password);
        assertThat(promoter.getName()).isEqualTo(name);
        assertThat(promoter.getDescription()).isEqualTo(description);
        assertThat(promoter.getLocation()).isEqualTo(location);
        assertThat(promoter.getMusicalGenres()).containsExactlyInAnyOrderElementsOf(genres);
        assertThat(promoter.getStatus()).isEqualTo(PromoterStatus.ACTIVE);
        assertThat(promoter.getCreatedAt()).isEqualTo(createdAt);
        assertThat(promoter.isActive()).isTrue();
    }

    // --- activate() ---

    @Test
    void activate_shouldSetStatusToActiveAndMarkAsActive() {
        Promoter promoter = Promoter.create(email, password, name, location);

        promoter.activate();

        assertThat(promoter.getStatus()).isEqualTo(PromoterStatus.ACTIVE);
        assertThat(promoter.isActive()).isTrue();
    }

    @Test
    void activate_shouldThrowPromoterAlreadyActiveExceptionWhenAlreadyActive() {
        Promoter promoter = Promoter.create(email, password, name, location);
        promoter.activate();

        assertThatExceptionOfType(PromoterAlreadyActiveException.class)
                .isThrownBy(promoter::activate);
    }

    // --- suspend() ---

    @Test
    void suspend_shouldSetStatusToSuspendedAndMarkAsInactive() {
        Promoter promoter = Promoter.create(email, password, name, location);
        promoter.activate();

        promoter.suspend();

        assertThat(promoter.getStatus()).isEqualTo(PromoterStatus.SUSPENDED);
        assertThat(promoter.isActive()).isFalse();
    }

    @Test
    void suspend_shouldThrowPromoterNotActiveExceptionWhenStatusIsPending() {
        Promoter promoter = Promoter.create(email, password, name, location);

        assertThatExceptionOfType(PromoterNotActiveException.class)
                .isThrownBy(promoter::suspend);
    }

    @Test
    void suspend_shouldThrowPromoterNotActiveExceptionWhenAlreadySuspended() {
        Promoter promoter = Promoter.create(email, password, name, location);
        promoter.activate();
        promoter.suspend();

        assertThatExceptionOfType(PromoterNotActiveException.class)
                .isThrownBy(promoter::suspend);
    }

    // --- addGenre() ---

    @Test
    void addGenre_shouldAddGenreToPromoter() {
        Promoter promoter = Promoter.create(email, password, name, location);

        promoter.addGenre(MusicGenre.ROCK);

        assertThat(promoter.getMusicalGenres()).containsExactly(MusicGenre.ROCK);
    }

    @Test
    void addGenre_shouldAddMultipleGenres() {
        Promoter promoter = Promoter.create(email, password, name, location);

        promoter.addGenre(MusicGenre.ROCK);
        promoter.addGenre(MusicGenre.JAZZ);
        promoter.addGenre(MusicGenre.TECHNO);

        assertThat(promoter.getMusicalGenres()).containsExactlyInAnyOrder(
                MusicGenre.ROCK, MusicGenre.JAZZ, MusicGenre.TECHNO);
    }

    @Test
    void addGenre_shouldNotAddDuplicateGenres() {
        Promoter promoter = Promoter.create(email, password, name, location);

        promoter.addGenre(MusicGenre.ROCK);
        promoter.addGenre(MusicGenre.ROCK);

        assertThat(promoter.getMusicalGenres()).hasSize(1);
    }

    @Test
    void addGenre_shouldThrowIllegalArgumentExceptionWhenGenreIsNull() {
        Promoter promoter = Promoter.create(email, password, name, location);

        assertThatIllegalArgumentException()
                .isThrownBy(() -> promoter.addGenre(null))
                .withMessageContaining("Genre can't be null");
    }

    // --- deleteGenre() ---

    @Test
    void deleteGenre_shouldRemoveGenreFromPromoter() {
        Promoter promoter = Promoter.create(email, password, name, location);
        promoter.addGenre(MusicGenre.ROCK);
        promoter.addGenre(MusicGenre.JAZZ);

        promoter.deleteGenre(MusicGenre.ROCK);

        assertThat(promoter.getMusicalGenres()).containsExactly(MusicGenre.JAZZ);
    }

    @Test
    void deleteGenre_shouldThrowGenreNotFoundExceptionWhenGenreNotPresent() {
        Promoter promoter = Promoter.create(email, password, name, location);

        assertThatExceptionOfType(GenreNotFoundException.class)
                .isThrownBy(() -> promoter.deleteGenre(MusicGenre.ROCK));
    }

    // --- getMusicalGenres() immutability ---

    @Test
    void getMusicalGenres_shouldReturnUnmodifiableSet() {
        Promoter promoter = Promoter.create(email, password, name, location);
        promoter.addGenre(MusicGenre.ROCK);

        assertThatExceptionOfType(UnsupportedOperationException.class)
                .isThrownBy(() -> promoter.getMusicalGenres().add(MusicGenre.POP));
    }
}

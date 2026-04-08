package com.gresk.modules.promoter.domain.model;

import com.gresk.modules.promoter.domain.exception.GenreNotFoundException;
import com.gresk.modules.promoter.domain.exception.PromoterAlreadyActiveException;
import com.gresk.modules.promoter.domain.exception.PromoterNotActiveException;
import com.gresk.modules.promoter.domain.model.valueobject.PromoterId;
import com.gresk.shared.domain.AccountStatus;
import com.gresk.shared.domain.MusicGenre;
import com.gresk.shared.domain.valueobject.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Set;

import static org.assertj.core.api.Assertions.*;

class PromoterTest {

    private PromoterId id;
    private Email email;
    private Name name;
    private Address address;
    private Description description;

    @BeforeEach
    void setUp() {
        id = PromoterId.generate();
        email = new Email("promoter@gresk.com");
        name = new Name("Club Nocturno");
        address = new Address("Calle Principal 123", City.of("Madrid"), "España");
        description = new Description("Club de música electrónica");
    }

    // --- create() ---

    @Test
    void create_shouldReturnPromoterWithPendingStatusAndInactive() {
        Promoter promoter = Promoter.create(id, email, name, address, null);

        assertThat(promoter.getStatus()).isEqualTo(AccountStatus.PENDING);
        assertThat(promoter.isActive()).isFalse();
    }

    @Test
    void create_shouldAssignProvidedId() {
        Promoter promoter = Promoter.create(id, email, name, address, null);

        assertThat(promoter.getId()).isEqualTo(id);
    }

    @Test
    void create_shouldAssignEmptyMusicGenres() {
        Promoter promoter = Promoter.create(id, email, name, address, null);

        assertThat(promoter.getMusicalGenres()).isEmpty();
    }

    @Test
    void create_shouldAssignEmptyDescriptionWhenNotProvided() {
        Promoter promoter = Promoter.create(id, email, name, address, null);

        assertThat(promoter.getDescription().value()).isEmpty();
    }

    @Test
    void create_shouldAssignDescriptionWhenProvided() {
        Promoter promoter = Promoter.create(id, email, name, address, description);

        assertThat(promoter.getDescription()).isEqualTo(description);
    }

    @Test
    void create_shouldAssignCreatedAtTimestamp() {
        LocalDateTime before = LocalDateTime.now().minusSeconds(1);

        Promoter promoter = Promoter.create(id, email, name, address, null);

        assertThat(promoter.getCreatedAt()).isAfter(before);
        assertThat(promoter.getCreatedAt()).isBefore(LocalDateTime.now().plusSeconds(1));
    }

    @Test
    void create_shouldPersistAllProvidedFields() {
        Promoter promoter = Promoter.create(id, email, name, address, description);

        assertThat(promoter.getId()).isEqualTo(id);
        assertThat(promoter.getEmail()).isEqualTo(email);
        assertThat(promoter.getName()).isEqualTo(name);
        assertThat(promoter.getAddress()).isEqualTo(address);
        assertThat(promoter.getDescription()).isEqualTo(description);
    }

    // --- updateBasicInfo() ---

    @Test
    void updateBasicInfo_shouldUpdateFields() {
        Promoter promoter = Promoter.create(id, email, name, address, description);
        Name newName = new Name("Nuevo Nombre");
        Address newAddress = new Address("Calle Nueva 456", City.of("Barcelona"), "España");
        Description newDescription = new Description("Nueva Descripción");

        promoter.updateBasicInfo(newName, newAddress, newDescription);

        assertThat(promoter.getName()).isEqualTo(newName);
        assertThat(promoter.getAddress()).isEqualTo(newAddress);
        assertThat(promoter.getDescription()).isEqualTo(newDescription);
    }

    // --- reconstitute() ---

    @Test
    void reconstitute_shouldRestoreAllFields() {
        Set<MusicGenre> genres = Set.of(MusicGenre.ROCK, MusicGenre.POP);
        LocalDateTime createdAt = LocalDateTime.of(2024, 1, 15, 10, 0);

        Promoter promoter = Promoter.reconstitute(id, email, name, address, description,
                genres, AccountStatus.ACTIVE, createdAt);

        assertThat(promoter.getId()).isEqualTo(id);
        assertThat(promoter.getEmail()).isEqualTo(email);
        assertThat(promoter.getName()).isEqualTo(name);
        assertThat(promoter.getDescription()).isEqualTo(description);
        assertThat(promoter.getAddress()).isEqualTo(address);
        assertThat(promoter.getMusicalGenres()).containsExactlyInAnyOrderElementsOf(genres);
        assertThat(promoter.getStatus()).isEqualTo(AccountStatus.ACTIVE);
        assertThat(promoter.getCreatedAt()).isEqualTo(createdAt);
        assertThat(promoter.isActive()).isTrue();
    }

    // --- activate() ---

    @Test
    void activate_shouldSetStatusToActive() {
        Promoter promoter = Promoter.create(id, email, name, address, null);

        promoter.activate();

        assertThat(promoter.getStatus()).isEqualTo(AccountStatus.ACTIVE);
        assertThat(promoter.isActive()).isTrue();
    }

    @Test
    void activate_shouldThrowPromoterAlreadyActiveExceptionWhenAlreadyActive() {
        Promoter promoter = Promoter.create(id, email, name, address, null);
        promoter.activate();

        assertThatExceptionOfType(PromoterAlreadyActiveException.class)
                .isThrownBy(promoter::activate);
    }

    // --- suspend() ---

    @Test
    void suspend_shouldSetStatusToSuspended() {
        Promoter promoter = Promoter.create(id, email, name, address, null);
        promoter.activate();

        promoter.suspend();

        assertThat(promoter.getStatus()).isEqualTo(AccountStatus.SUSPENDED);
        assertThat(promoter.isActive()).isFalse();
    }

    @Test
    void suspend_shouldThrowPromoterNotActiveExceptionWhenStatusIsPending() {
        Promoter promoter = Promoter.create(id, email, name, address, null);

        assertThatExceptionOfType(PromoterNotActiveException.class)
                .isThrownBy(promoter::suspend);
    }

    @Test
    void suspend_shouldThrowPromoterNotActiveExceptionWhenAlreadySuspended() {
        Promoter promoter = Promoter.create(id, email, name, address, null);
        promoter.activate();
        promoter.suspend();

        assertThatExceptionOfType(PromoterNotActiveException.class)
                .isThrownBy(promoter::suspend);
    }

    // --- addGenre() ---

    @Test
    void addGenre_shouldAddGenreToActivePromoter() {
        Promoter promoter = Promoter.create(id, email, name, address, null);
        promoter.activate();

        promoter.addGenre(MusicGenre.ROCK);

        assertThat(promoter.getMusicalGenres()).containsExactly(MusicGenre.ROCK);
    }

    @Test
    void addGenre_shouldThrowPromoterNotActiveExceptionWhenNotActive() {
        Promoter promoter = Promoter.create(id, email, name, address, null);

        assertThatExceptionOfType(PromoterNotActiveException.class)
                .isThrownBy(() -> promoter.addGenre(MusicGenre.ROCK));
    }

    @Test
    void addGenre_shouldNotAddDuplicateGenres() {
        Promoter promoter = Promoter.create(id, email, name, address, null);
        promoter.activate();

        promoter.addGenre(MusicGenre.ROCK);
        promoter.addGenre(MusicGenre.ROCK);

        assertThat(promoter.getMusicalGenres()).hasSize(1);
    }

    @Test
    void addGenre_shouldThrowIllegalArgumentExceptionWhenGenreIsNull() {
        Promoter promoter = Promoter.create(id, email, name, address, null);
        promoter.activate();

        assertThatIllegalArgumentException()
                .isThrownBy(() -> promoter.addGenre(null))
                .withMessageContaining("Genre can't be null");
    }

    // --- deleteGenre() ---

    @Test
    void deleteGenre_shouldRemoveGenreFromActivePromoter() {
        Promoter promoter = Promoter.create(id, email, name, address, null);
        promoter.activate();
        promoter.addGenre(MusicGenre.ROCK);
        promoter.addGenre(MusicGenre.JAZZ);

        promoter.deleteGenre(MusicGenre.ROCK);

        assertThat(promoter.getMusicalGenres()).containsExactly(MusicGenre.JAZZ);
    }

    @Test
    void deleteGenre_shouldThrowPromoterNotActiveExceptionWhenNotActive() {
        Promoter promoter = Promoter.create(id, email, name, address, null);
        // Not activated
        assertThatExceptionOfType(PromoterNotActiveException.class)
                .isThrownBy(() -> promoter.deleteGenre(MusicGenre.ROCK));
    }

    @Test
    void deleteGenre_shouldThrowGenreNotFoundExceptionWhenGenreNotPresent() {
        Promoter promoter = Promoter.create(id, email, name, address, null);
        promoter.activate();

        assertThatExceptionOfType(GenreNotFoundException.class)
                .isThrownBy(() -> promoter.deleteGenre(MusicGenre.ROCK));
    }

    // --- getMusicalGenres() immutability ---

    @Test
    void getMusicalGenres_shouldReturnUnmodifiableSet() {
        Promoter promoter = Promoter.create(id, email, name, address, null);
        promoter.activate();
        promoter.addGenre(MusicGenre.ROCK);

        assertThatExceptionOfType(UnsupportedOperationException.class)
                .isThrownBy(() -> promoter.getMusicalGenres().add(MusicGenre.POP));
    }
}

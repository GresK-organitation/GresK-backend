package com.gresk.modules.promoter.application.command;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

class RegisterPromoterCommandTest {

    @Test
    void constructor_shouldCreateCommandWithAllFields() {
        List<String> genres = List.of("ROCK", "TECHNO");

        RegisterPromoterCommand command = new RegisterPromoterCommand(
                "promoter@gresk.com",
                "securePassword123",
                "Club Nocturno",
                "Madrid",
                "España",
                "Calle Gran Vía 1",
                "Club de música electrónica",
                genres
        );

        assertThat(command.email()).isEqualTo("promoter@gresk.com");
        assertThat(command.rawPassword()).isEqualTo("securePassword123");
        assertThat(command.name()).isEqualTo("Club Nocturno");
        assertThat(command.city()).isEqualTo("Madrid");
        assertThat(command.country()).isEqualTo("España");
        assertThat(command.address()).isEqualTo("Calle Gran Vía 1");
        assertThat(command.description()).isEqualTo("Club de música electrónica");
        assertThat(command.musicalGenres()).containsExactly("ROCK", "TECHNO");
    }

    @Test
    void constructor_shouldAllowNullOptionalFields() {
        RegisterPromoterCommand command = new RegisterPromoterCommand(
                "promoter@gresk.com",
                "securePassword123",
                "Club Nocturno",
                "Madrid",
                "España",
                null,
                null,
                null
        );

        assertThat(command.address()).isNull();
        assertThat(command.description()).isNull();
        assertThat(command.musicalGenres()).isNull();
    }

    @Test
    void constructor_shouldAllowEmptyMusicalGenresList() {
        RegisterPromoterCommand command = new RegisterPromoterCommand(
                "promoter@gresk.com",
                "securePassword123",
                "Club Nocturno",
                "Madrid",
                "España",
                null,
                null,
                List.of()
        );

        assertThat(command.musicalGenres()).isEmpty();
    }

    @Test
    void equality_shouldBeBasedOnAllFields() {
        RegisterPromoterCommand command1 = new RegisterPromoterCommand(
                "promoter@gresk.com", "pass", "Club", "Madrid", "España", null, null, null);
        RegisterPromoterCommand command2 = new RegisterPromoterCommand(
                "promoter@gresk.com", "pass", "Club", "Madrid", "España", null, null, null);

        assertThat(command1).isEqualTo(command2);
        assertThat(command1.hashCode()).isEqualTo(command2.hashCode());
    }

    @Test
    void equality_shouldReturnFalseWhenEmailDiffers() {
        RegisterPromoterCommand command1 = new RegisterPromoterCommand(
                "a@gresk.com", "pass", "Club", "Madrid", "España", null, null, null);
        RegisterPromoterCommand command2 = new RegisterPromoterCommand(
                "b@gresk.com", "pass", "Club", "Madrid", "España", null, null, null);

        assertThat(command1).isNotEqualTo(command2);
    }
}
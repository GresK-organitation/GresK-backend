package com.gresk.modules.promoter.domain.valueObjects;

import com.gresk.modules.promoter.valueObjects.Location;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class LocationTest {

    @Test
    void constructor_shouldCreateLocationWithCityAndCountry() {
        Location location = new Location("Madrid", "España", null);

        assertThat(location.city()).isEqualTo("Madrid");
        assertThat(location.country()).isEqualTo("España");
        assertThat(location.address()).isNull();
    }

    @Test
    void constructor_shouldCreateLocationWithAddress() {
        Location location = new Location("Madrid", "España", "Calle Gran Vía 1");

        assertThat(location.city()).isEqualTo("Madrid");
        assertThat(location.country()).isEqualTo("España");
        assertThat(location.address()).isEqualTo("Calle Gran Vía 1");
    }

    @Test
    void constructor_shouldTrimAllFields() {
        Location location = new Location("  Madrid  ", "  España  ", "  Calle Mayor 5  ");

        assertThat(location.city()).isEqualTo("Madrid");
        assertThat(location.country()).isEqualTo("España");
        assertThat(location.address()).isEqualTo("Calle Mayor 5");
    }

    @Test
    void constructor_shouldStoreNullAddressWhenBlank() {
        Location location = new Location("Madrid", "España", "   ");

        assertThat(location.address()).isNull();
    }

    @Test
    void constructor_shouldThrowWhenCityIsNull() {
        assertThatIllegalArgumentException()
                .isThrownBy(() -> new Location(null, "España", null))
                .withMessageContaining("City cannot be empty");
    }

    @Test
    void constructor_shouldThrowWhenCityIsBlank() {
        assertThatIllegalArgumentException()
                .isThrownBy(() -> new Location("   ", "España", null))
                .withMessageContaining("City cannot be empty");
    }

    @Test
    void constructor_shouldThrowWhenCountryIsNull() {
        assertThatIllegalArgumentException()
                .isThrownBy(() -> new Location("Madrid", null, null))
                .withMessageContaining("Country cannot be empty");
    }

    @Test
    void constructor_shouldThrowWhenCountryIsBlank() {
        assertThatIllegalArgumentException()
                .isThrownBy(() -> new Location("Madrid", "   ", null))
                .withMessageContaining("Country cannot be empty");
    }

    @Test
    void toString_shouldFormatWithoutAddressWhenNull() {
        Location location = new Location("Madrid", "España", null);

        assertThat(location.toString()).isEqualTo("Madrid, España");
    }

    @Test
    void toString_shouldFormatWithAddressWhenPresent() {
        Location location = new Location("Madrid", "España", "Calle Mayor 5");

        assertThat(location.toString()).isEqualTo("Calle Mayor 5, Madrid (España)");
    }

    @Test
    void equality_shouldBeBasedOnAllFields() {
        Location location1 = new Location("Madrid", "España", "Calle Mayor 5");
        Location location2 = new Location("Madrid", "España", "Calle Mayor 5");

        assertThat(location1).isEqualTo(location2);
        assertThat(location1.hashCode()).isEqualTo(location2.hashCode());
    }

    @Test
    void equality_shouldReturnFalseForDifferentCity() {
        Location location1 = new Location("Madrid", "España", null);
        Location location2 = new Location("Barcelona", "España", null);

        assertThat(location1).isNotEqualTo(location2);
    }

    @Test
    void equality_shouldReturnFalseForDifferentCountry() {
        Location location1 = new Location("Madrid", "España", null);
        Location location2 = new Location("Madrid", "Portugal", null);

        assertThat(location1).isNotEqualTo(location2);
    }

    @Test
    void equality_shouldReturnFalseForDifferentAddress() {
        Location location1 = new Location("Madrid", "España", "Calle A");
        Location location2 = new Location("Madrid", "España", "Calle B");

        assertThat(location1).isNotEqualTo(location2);
    }
}

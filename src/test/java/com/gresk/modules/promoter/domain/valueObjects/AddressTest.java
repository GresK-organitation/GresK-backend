package com.gresk.modules.promoter.domain.valueObjects;

import com.gresk.shared.domain.valueobject.Address;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class AddressTest {

    @Test
    void constructor_shouldCreateLocationWithCityAndCountry() {
        Address address = new Address("Madrid", "España", null);

        assertThat(address.city()).isEqualTo("Madrid");
        assertThat(address.country()).isEqualTo("España");
        assertThat(address.address()).isNull();
    }

    @Test
    void constructor_shouldCreateLocationWithAddress() {
        Address address = new Address("Madrid", "España", "Calle Gran Vía 1");

        assertThat(address.city()).isEqualTo("Madrid");
        assertThat(address.country()).isEqualTo("España");
        assertThat(address.address()).isEqualTo("Calle Gran Vía 1");
    }

    @Test
    void constructor_shouldTrimAllFields() {
        Address address = new Address("  Madrid  ", "  España  ", "  Calle Mayor 5  ");

        assertThat(address.city()).isEqualTo("Madrid");
        assertThat(address.country()).isEqualTo("España");
        assertThat(address.address()).isEqualTo("Calle Mayor 5");
    }

    @Test
    void constructor_shouldStoreNullAddressWhenBlank() {
        Address address = new Address("Madrid", "España", "   ");

        assertThat(address.address()).isNull();
    }

    @Test
    void constructor_shouldThrowWhenCityIsNull() {
        assertThatIllegalArgumentException()
                .isThrownBy(() -> new Address(null, "España", null))
                .withMessageContaining("City cannot be empty");
    }

    @Test
    void constructor_shouldThrowWhenCityIsBlank() {
        assertThatIllegalArgumentException()
                .isThrownBy(() -> new Address("   ", "España", null))
                .withMessageContaining("City cannot be empty");
    }

    @Test
    void constructor_shouldThrowWhenCountryIsNull() {
        assertThatIllegalArgumentException()
                .isThrownBy(() -> new Address("Madrid", null, null))
                .withMessageContaining("Country cannot be empty");
    }

    @Test
    void constructor_shouldThrowWhenCountryIsBlank() {
        assertThatIllegalArgumentException()
                .isThrownBy(() -> new Address("Madrid", "   ", null))
                .withMessageContaining("Country cannot be empty");
    }

    @Test
    void toString_shouldFormatWithoutAddressWhenNull() {
        Address address = new Address("Madrid", "España", null);

        assertThat(address.toString()).isEqualTo("Madrid, España");
    }

    @Test
    void toString_shouldFormatWithAddressWhenPresent() {
        Address address = new Address("Madrid", "España", "Calle Mayor 5");

        assertThat(address.toString()).isEqualTo("Calle Mayor 5, Madrid (España)");
    }

    @Test
    void equality_shouldBeBasedOnAllFields() {
        Address address1 = new Address("Madrid", "España", "Calle Mayor 5");
        Address address2 = new Address("Madrid", "España", "Calle Mayor 5");

        assertThat(address1).isEqualTo(address2);
        assertThat(address1.hashCode()).isEqualTo(address2.hashCode());
    }

    @Test
    void equality_shouldReturnFalseForDifferentCity() {
        Address address1 = new Address("Madrid", "España", null);
        Address address2 = new Address("Barcelona", "España", null);

        assertThat(address1).isNotEqualTo(address2);
    }

    @Test
    void equality_shouldReturnFalseForDifferentCountry() {
        Address address1 = new Address("Madrid", "España", null);
        Address address2 = new Address("Madrid", "Portugal", null);

        assertThat(address1).isNotEqualTo(address2);
    }

    @Test
    void equality_shouldReturnFalseForDifferentAddress() {
        Address address1 = new Address("Madrid", "España", "Calle A");
        Address address2 = new Address("Madrid", "España", "Calle B");

        assertThat(address1).isNotEqualTo(address2);
    }
}

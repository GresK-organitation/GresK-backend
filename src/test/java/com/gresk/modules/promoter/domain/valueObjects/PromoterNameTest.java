package com.gresk.modules.promoter.domain.valueObjects;

import com.gresk.modules.promoter.domain.exception.InvalidPromoterNameException;
import com.gresk.modules.promoter.domain.valueobject.PromoterName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class PromoterNameTest {

    @Test
    void constructor_shouldCreateNameWithValidValue() {
        PromoterName name = new PromoterName("Club Nocturno");

        assertThat(name.value()).isEqualTo("Club Nocturno");
    }

    @Test
    void constructor_shouldTrimWhitespace() {
        PromoterName name = new PromoterName("  Club Nocturno  ");

        assertThat(name.value()).isEqualTo("Club Nocturno");
    }

    @Test
    void constructor_shouldThrowInvalidPromoterNameExceptionWhenValueIsNull() {
        assertThatExceptionOfType(InvalidPromoterNameException.class)
                .isThrownBy(() -> new PromoterName(null))
                .withMessageContaining("El nombre no puede estar vacío");
    }

    @Test
    void constructor_shouldThrowInvalidPromoterNameExceptionWhenValueIsBlank() {
        assertThatExceptionOfType(InvalidPromoterNameException.class)
                .isThrownBy(() -> new PromoterName("   "))
                .withMessageContaining("El nombre no puede estar vacío");
    }

    @Test
    void constructor_shouldThrowWhenNameExceedsMaxLength() {
        String tooLong = "A".repeat(101);

        assertThatIllegalArgumentException()
                .isThrownBy(() -> new PromoterName(tooLong));
    }

    @Test
    void constructor_shouldAcceptNameWithExactlyMaxLength() {
        String exactMax = "A".repeat(100);

        assertThatNoException().isThrownBy(() -> new PromoterName(exactMax));
    }

    @Test
    void equality_shouldBeBasedOnValue() {
        PromoterName name1 = new PromoterName("Club Nocturno");
        PromoterName name2 = new PromoterName("Club Nocturno");

        assertThat(name1).isEqualTo(name2);
        assertThat(name1.hashCode()).isEqualTo(name2.hashCode());
    }

    @Test
    void equality_shouldReturnFalseForDifferentNames() {
        PromoterName name1 = new PromoterName("Club A");
        PromoterName name2 = new PromoterName("Club B");

        assertThat(name1).isNotEqualTo(name2);
    }

    @Test
    void toString_shouldReturnNameValue() {
        PromoterName name = new PromoterName("Club Nocturno");

        assertThat(name.toString()).isEqualTo("Club Nocturno");
    }
}

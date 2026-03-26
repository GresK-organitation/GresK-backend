package com.gresk.modules.promoter.domain.valueObjects;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class DescriptionTest {

    @Test
    void constructor_shouldCreateDescriptionWithValidValue() {
        Description description = new Description("Una descripción válida");

        assertThat(description.value()).isEqualTo("Una descripción válida");
    }

    @Test
    void constructor_shouldTrimWhitespace() {
        Description description = new Description("  Descripción con espacios  ");

        assertThat(description.value()).isEqualTo("Descripción con espacios");
    }

    @Test
    void constructor_shouldStoreNullWhenValueIsNull() {
        Description description = new Description(null);

        assertThat(description.value()).isNull();
    }

    @Test
    void constructor_shouldStoreNullWhenValueIsBlank() {
        Description description = new Description("   ");

        assertThat(description.value()).isNull();
    }

    @Test
    void constructor_shouldThrowWhenValueExceedsMaxLength() {
        String tooLong = "A".repeat(601);

        assertThatIllegalArgumentException()
                .isThrownBy(() -> new Description(tooLong))
                .withMessageContaining("600");
    }

    @Test
    void constructor_shouldAcceptDescriptionWithExactlyMaxLength() {
        String exactMax = "A".repeat(600);

        assertThatNoException().isThrownBy(() -> new Description(exactMax));
    }

    @Test
    void toString_shouldReturnValueWhenPresent() {
        Description description = new Description("Mi descripción");

        assertThat(description.toString()).isEqualTo("Mi descripción");
    }

    @Test
    void toString_shouldReturnEmptyStringWhenValueIsNull() {
        Description description = new Description(null);

        assertThat(description.toString()).isEqualTo("");
    }

    @Test
    void equality_shouldBeBasedOnValue() {
        Description desc1 = new Description("Misma descripción");
        Description desc2 = new Description("Misma descripción");

        assertThat(desc1).isEqualTo(desc2);
        assertThat(desc1.hashCode()).isEqualTo(desc2.hashCode());
    }

    @Test
    void equality_shouldBeEqualWhenBothAreNull() {
        Description desc1 = new Description(null);
        Description desc2 = new Description(null);

        assertThat(desc1).isEqualTo(desc2);
    }

    @Test
    void equality_shouldReturnFalseForDifferentValues() {
        Description desc1 = new Description("Descripción A");
        Description desc2 = new Description("Descripción B");

        assertThat(desc1).isNotEqualTo(desc2);
    }
}

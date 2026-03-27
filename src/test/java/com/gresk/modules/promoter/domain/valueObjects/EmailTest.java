package com.gresk.modules.promoter.domain.valueObjects;

import com.gresk.modules.promoter.valueObjects.Email;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.*;

class EmailTest {

    @Test
    void constructor_shouldCreateEmailWithValidValue() {
        Email email = new Email("user@example.com");

        assertThat(email.value()).isEqualTo("user@example.com");
    }

    @Test
    void constructor_shouldNormalizeToLowerCase() {
        Email email = new Email("User@EXAMPLE.COM");

        assertThat(email.value()).isEqualTo("user@example.com");
    }

    @Test
    void constructor_shouldTrimWhitespace() {
        Email email = new Email("  user@example.com  ");

        assertThat(email.value()).isEqualTo("user@example.com");
    }

    @Test
    void constructor_shouldThrowWhenValueIsNull() {
        assertThatIllegalArgumentException()
                .isThrownBy(() -> new Email(null))
                .withMessageContaining("Email can't be empty");
    }

    @Test
    void constructor_shouldThrowWhenValueIsBlank() {
        assertThatIllegalArgumentException()
                .isThrownBy(() -> new Email("   "))
                .withMessageContaining("Email can't be empty");
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "notanemail",
            "missing@domain",
            "@nodomain.com",
            "spaces in@email.com",
            "double@@email.com"
    })
    void constructor_shouldThrowWhenFormatIsInvalid(String invalidEmail) {
        assertThatIllegalArgumentException()
                .isThrownBy(() -> new Email(invalidEmail))
                .withMessageContaining("Email format invalid");
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "user@example.com",
            "user.name+tag@sub.domain.org",
            "user_123@mail.co"
    })
    void constructor_shouldAcceptValidEmailFormats(String validEmail) {
        assertThatNoException().isThrownBy(() -> new Email(validEmail));
    }

    @Test
    void equality_shouldBeBasedOnNormalizedValue() {
        Email email1 = new Email("user@example.com");
        Email email2 = new Email("USER@EXAMPLE.COM");

        assertThat(email1).isEqualTo(email2);
        assertThat(email1.hashCode()).isEqualTo(email2.hashCode());
    }

    @Test
    void equality_shouldReturnFalseForDifferentEmails() {
        Email email1 = new Email("user1@example.com");
        Email email2 = new Email("user2@example.com");

        assertThat(email1).isNotEqualTo(email2);
    }

    @Test
    void toString_shouldReturnEmailValue() {
        Email email = new Email("user@example.com");

        assertThat(email.toString()).isEqualTo("user@example.com");
    }
}

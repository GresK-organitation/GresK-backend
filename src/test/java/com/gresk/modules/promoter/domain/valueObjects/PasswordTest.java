package com.gresk.modules.promoter.domain.valueObjects;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class PasswordTest {

    @Test
    void constructor_shouldCreatePasswordWithHashedValue() {
        Password password = new Password("$2a$10$hashedpasswordvalue");

        assertThat(password.hashedValue()).isEqualTo("$2a$10$hashedpasswordvalue");
    }

    @Test
    void constructor_shouldThrowWhenHashedValueIsNull() {
        assertThatIllegalArgumentException()
                .isThrownBy(() -> new Password(null));
    }

    @Test
    void constructor_shouldThrowWhenHashedValueIsBlank() {
        assertThatIllegalArgumentException()
                .isThrownBy(() -> new Password("   "));
    }

    @Test
    void toString_shouldNotExposeHashedValue() {
        Password password = new Password("$2a$10$secrethash");

        assertThat(password.toString()).isEqualTo("[PROTECTED]");
        assertThat(password.toString()).doesNotContain("secrethash");
    }

    @Test
    void equality_shouldBeBasedOnHashedValue() {
        Password password1 = new Password("$2a$10$samehash");
        Password password2 = new Password("$2a$10$samehash");

        assertThat(password1).isEqualTo(password2);
        assertThat(password1.hashCode()).isEqualTo(password2.hashCode());
    }

    @Test
    void equality_shouldReturnFalseForDifferentHashes() {
        Password password1 = new Password("$2a$10$hash1");
        Password password2 = new Password("$2a$10$hash2");

        assertThat(password1).isNotEqualTo(password2);
    }
}

package com.gresk.modules.promoter.domain.valueObjects;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.*;

class PromoterIdTest {

    @Test
    void generate_shouldCreatePromoterIdWithValidUUID() {
        PromoterId id = PromoterId.generate();

        assertThat(id).isNotNull();
        assertThat(id.value()).isNotNull();
        assertThat(id.value()).isInstanceOf(UUID.class);
    }

    @Test
    void generate_shouldCreateUniqueIds() {
        PromoterId id1 = PromoterId.generate();
        PromoterId id2 = PromoterId.generate();

        assertThat(id1).isNotEqualTo(id2);
    }

    @Test
    void of_shouldCreatePromoterIdFromValidUUIDString() {
        String uuidStr = UUID.randomUUID().toString();

        PromoterId id = PromoterId.of(uuidStr);

        assertThat(id.value().toString()).isEqualTo(uuidStr);
    }

    @Test
    void of_shouldThrowWhenStringIsNotValidUUID() {
        assertThatIllegalArgumentException()
                .isThrownBy(() -> PromoterId.of("not-a-uuid"))
                .withMessageContaining("Invalid PromoterId format");
    }

    @Test
    void constructor_shouldThrowWhenValueIsNull() {
        assertThatIllegalArgumentException()
                .isThrownBy(() -> new PromoterId(null))
                .withMessageContaining("PromoterId cannot be null");
    }

    @Test
    void toString_shouldReturnUUIDString() {
        UUID uuid = UUID.randomUUID();
        PromoterId id = new PromoterId(uuid);

        assertThat(id.toString()).isEqualTo(uuid.toString());
    }

    @Test
    void equality_shouldBeBasedOnValue() {
        UUID uuid = UUID.randomUUID();
        PromoterId id1 = new PromoterId(uuid);
        PromoterId id2 = new PromoterId(uuid);

        assertThat(id1).isEqualTo(id2);
        assertThat(id1.hashCode()).isEqualTo(id2.hashCode());
    }
}
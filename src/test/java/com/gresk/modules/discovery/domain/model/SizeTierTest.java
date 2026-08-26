package com.gresk.modules.discovery.domain.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SizeTierTest {

    @Test
    void fromPopularity_clasificaPorUmbrales() {
        assertEquals(SizeTier.NO_SPOTIFY, SizeTier.fromPopularity(null));
        assertEquals(SizeTier.MICRO, SizeTier.fromPopularity(0));
        assertEquals(SizeTier.MICRO, SizeTier.fromPopularity(14));
        assertEquals(SizeTier.SMALL, SizeTier.fromPopularity(15));
        assertEquals(SizeTier.SMALL, SizeTier.fromPopularity(29));
        assertEquals(SizeTier.EMERGING, SizeTier.fromPopularity(30));
        assertEquals(SizeTier.EMERGING, SizeTier.fromPopularity(44));
        assertEquals(SizeTier.GROWING, SizeTier.fromPopularity(45));
        assertEquals(SizeTier.GROWING, SizeTier.fromPopularity(59));
        assertEquals(SizeTier.ESTABLISHED, SizeTier.fromPopularity(60));
        assertEquals(SizeTier.ESTABLISHED, SizeTier.fromPopularity(100));
    }
}

package com.gresk.modules.rider.domain.model.valueobject;

import java.math.BigDecimal;

public record StageDimensions(
        BigDecimal widthMeters,
        BigDecimal depthMeters,
        BigDecimal minHeightMeters,
        Integer powerOutlets,
        boolean hasDrumRiser
) {}

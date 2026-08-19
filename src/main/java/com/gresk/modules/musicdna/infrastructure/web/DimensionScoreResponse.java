package com.gresk.modules.musicdna.infrastructure.web;

import java.math.BigDecimal;

public record DimensionScoreResponse(BigDecimal score, String label) {}

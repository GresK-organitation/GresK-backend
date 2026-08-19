package com.gresk.modules.musicdna.infrastructure.web;

public record UserMusicDnaResponse(
        String                  userId,
        DimensionScoreResponse  intensidad,
        DimensionScoreResponse  diversidad,
        DimensionScoreResponse  criticidad,
        DimensionScoreResponse  localismo,
        DimensionScoreResponse  antiguedad,
        DimensionScoreResponse  autenticidad,
        String                  summaryPhrase,
        String                  calculatedAt
) {}

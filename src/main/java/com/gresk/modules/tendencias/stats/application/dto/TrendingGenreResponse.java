package com.gresk.modules.tendencias.stats.application.dto;

public record TrendingGenreResponse(String genre, long currentCount, long previousCount, double growthPercent) {
}

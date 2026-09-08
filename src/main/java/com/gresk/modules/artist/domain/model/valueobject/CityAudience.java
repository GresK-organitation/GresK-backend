package com.gresk.modules.artist.domain.model.valueobject;

public record CityAudience(String city, String country, Integer audienceScore, TractionSource source) {

    public CityAudience {
        if (city == null || city.isBlank()) {
            throw new IllegalArgumentException("city is required");
        }
        if (country == null || country.isBlank()) {
            throw new IllegalArgumentException("country is required");
        }
        if (source == null) {
            throw new IllegalArgumentException("source is required");
        }
        city = city.trim();
        country = country.trim();
    }

    public static CityAudience of(String city, String country, Integer audienceScore, TractionSource source) {
        return new CityAudience(city, country, audienceScore, source);
    }
}

package com.gresk.modules.booking.domain.model.valueobject;

/**
 * Territorio geográfico simplificado (país/región/ciudad + radio opcional en km).
 * <p>
 * Limitación conocida: {@link #overlaps(Territory)} compara solo por ciudad/región,
 * sin cálculo geodésico real (no hay coordenadas). Cuando exista un aggregate {@code Venue}
 * real con {@code Coordinates}, esta clase podrá enriquecerse para hacer overlap por distancia.
 */
public record Territory(String country, String region, String city, Double radiusKm) {

    public Territory {
        if (country == null || country.isBlank()) {
            throw new IllegalArgumentException("Territory country must not be blank");
        }
        if (city == null || city.isBlank()) {
            throw new IllegalArgumentException("Territory city must not be blank");
        }
        if (radiusKm != null && radiusKm <= 0) {
            throw new IllegalArgumentException("Territory radiusKm must be > 0 when present");
        }
    }

    public static Territory of(String country, String region, String city, Double radiusKm) {
        return new Territory(country, region, city, radiusKm);
    }

    /**
     * Coincidencia exacta de ciudad (case-insensitive) siempre cuenta como solapamiento.
     * Si alguno de los dos define {@code radiusKm}, se trata como solapamiento a nivel
     * región cuando país+región coinciden (aproximación sin geodesia real).
     */
    public boolean overlaps(Territory other) {
        if (other == null) return false;
        if (city.equalsIgnoreCase(other.city) && country.equalsIgnoreCase(other.country)) {
            return true;
        }
        boolean anyRadiusDefined = radiusKm != null || other.radiusKm != null;
        if (!anyRadiusDefined) return false;
        return country.equalsIgnoreCase(other.country)
                && region != null && other.region != null
                && region.equalsIgnoreCase(other.region);
    }
}

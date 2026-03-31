package com.gresk.modules.promoter.domain.valueobject;

import java.util.Objects;

public final class Location {

    public final String city;
    public final String country;
    public final String address;

    public Location(String city, String country, String address) {
        if (city == null || city.isBlank()) {
            throw new IllegalArgumentException("City cannot be empty");
        }
        if (country == null || country.isBlank()) {
            throw new IllegalArgumentException("Country cannot be empty");
        }

        this.city = city.trim();
        this.country = country.trim();
        this.address = (address == null || address.isBlank())
                ? null
                : address.trim();
    }

    public String city() { return city; }
    public String country() { return country; }
    public String address() { return address; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Location l)) return false;
        return Objects.equals(city, l.city)
                && Objects.equals(country, l.country)
                && Objects.equals(address, l.address);
    }
    @Override
    public int hashCode() {
        return Objects.hash(city, country, address);
    }

    @Override public String toString() {
        return address != null
                ? String.format("%s, %s (%s)", address, city, country)
                : String.format("%s, %s", city, country);
    }


}

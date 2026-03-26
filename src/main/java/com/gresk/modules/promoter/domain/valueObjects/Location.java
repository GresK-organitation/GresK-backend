package com.gresk.modules.promoter.domain.valueObjects;

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

        this.address = (address == null || address.isBlank())
                ? "No address"
                : address.trim();

        this.city = city.trim();
        this.country = country.trim();
    }

    public String city() { return city; }
    public String country() { return country; }
    public String address() { return address; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Location location)) return false;
        return Objects.equals(city, location.city) &&
                Objects.equals(country, location.country) &&
                Objects.equals(address, location.address);
    }

    @Override
    public int hashCode() {
        return Objects.hash(city, country, address);
    }

    @Override
    public String toString() {
        return String.format("%s, %s (%s)", address, city, country);
    }


}

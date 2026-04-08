package com.gresk.shared.domain.valueobject;

import java.util.Objects;

public final class Address {

    private final String street;
    private final City city;
    private final String country;

    public Address(String street, City city, String country) {
        if (street == null || street.isBlank()) {
            throw new IllegalArgumentException("Street cannot be empty");
        }
        if (city == null) {
            throw new IllegalArgumentException("City cannot be null");
        }
        if (country == null || country.isBlank()) {
            throw new IllegalArgumentException("Country cannot be empty");
        }

        this.street = street.trim();
        this.city = city;
        this.country = country.trim();
    }

    public String street() { return street; }
    public City city() { return city; }
    public String country() { return country; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Address address)) return false;
        return Objects.equals(street, address.street)
                && Objects.equals(city, address.city)
                && Objects.equals(country, address.country);
    }

    @Override
    public int hashCode() {
        return Objects.hash(street, city, country);
    }

    @Override public String toString() {
        return String.format("%s, %s (%s)", street, city.value(), country);
    }
}

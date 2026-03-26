package com.gresk.modules.promoter.domain.valueObjects;

import java.util.Objects;
import java.util.regex.Pattern;

public final class Email {

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    private final String value;

    public Email(String value) {
        if(value == null || value.isBlank()){
            throw new IllegalArgumentException("Email can't be empty");
        }
        if (!EMAIL_PATTERN.matcher(value.trim()).matches()) {
            throw new IllegalArgumentException("Email format invalid: " + value);
        }

        this.value = value.trim().toLowerCase();
    }

    public String value(){
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Email email)) return false;
        return Objects.equals(value, email.value);
    }

    @Override
    public int hashCode() { return Objects.hash(value); }

    @Override
    public String toString() { return value; }
}

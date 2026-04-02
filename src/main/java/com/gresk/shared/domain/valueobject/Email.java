package com.gresk.shared.domain.valueobject;

import java.util.regex.Pattern;

public record Email(String value) {

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?$",
                    Pattern.CASE_INSENSITIVE);

    public Email {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Email can't be empty");
        }

        value = value.trim().toLowerCase();

        if (!EMAIL_PATTERN.matcher(value).matches()) {
            throw new IllegalArgumentException("Email format invalid");
        }
    }


    public static Email of(String value) {
        return new Email(value);
    }

    public static Email reconstitute(String value) {
        return new Email(value);
    }

    @Override
    public String toString() {
        return value;
    }
}
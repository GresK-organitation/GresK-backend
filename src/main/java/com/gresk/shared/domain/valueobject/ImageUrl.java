package com.gresk.shared.domain.valueobject;

import java.net.URI;

public record ImageUrl(String value) {

    public ImageUrl {
        if (value == null || value.isBlank()) {
            value = "";
        } else {
            value = value.trim();
            validateHttpUrl(value);
        }
    }

    public boolean isEmpty() {
        return value.isEmpty();
    }

    public static ImageUrl of(String value) {
        return new ImageUrl(value);
    }

    public static ImageUrl reconstitute(String value) {
        return new ImageUrl(value);
    }

    @Override
    public String toString() {
        return value;
    }

    private static void validateHttpUrl(String value) {
        final URI uri;
        try {
            uri = URI.create(value);
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException("ImageUrl must be a valid URI", ex);
        }

        String scheme = uri.getScheme();
        if (scheme == null || !(scheme.equalsIgnoreCase("http") || scheme.equalsIgnoreCase("https"))) {
            throw new IllegalArgumentException("ImageUrl must use http/https");
        }

        if (uri.getHost() == null || uri.getHost().isBlank()) {
            throw new IllegalArgumentException("ImageUrl must include a host");
        }
    }
}

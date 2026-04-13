package com.gresk.modules.event.domain.model;

import com.gresk.shared.domain.valueobject.ImageUrl;

import java.util.Objects;

/**
 * VO temporal dentro de Event hasta que Artist tenga su propio módulo.
 */
public record Artist(String name, ImageUrl imageUrl) {

    public Artist {
        Objects.requireNonNull(name, "Artist name must not be null");
        name = name.trim();
        if (name.isBlank()) {
            throw new IllegalArgumentException("Artist name must not be blank");
        }
        // imageUrl puede ser null (artista sin foto)
    }

    public static Artist of(String name, ImageUrl imageUrl) {
        return new Artist(name, imageUrl);
    }

    public static Artist of(String name) {
        return new Artist(name, null);
    }

    @Override
    public String toString() {
        return name;
    }
}

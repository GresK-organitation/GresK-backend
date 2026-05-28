package com.gresk.modules.artist.infrastructure.web;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

import java.util.List;
import java.util.Set;

public record CreateArtistRequest(

        @NotBlank
        @Size(max = 60)
        String name,

        @NotBlank
        String origin,

        @NotEmpty
        Set<String> genres,

        String imageUrl,

        @NotBlank
        @Size(max = 600)
        String bio,

        @NotBlank
        String status,

        String fee,

        String followers,

        Set<String> tags,

        @NotBlank
        String contact,

        String instagramUrl,

        String spotifyUrl,

        // Campos opcionales de vinculación Spotify (rellenados desde la búsqueda previa)
        String       spotifyArtistId,
        String       spotifyName,
        String       spotifyImageUrl,
        List<String> spotifyGenres
) {}

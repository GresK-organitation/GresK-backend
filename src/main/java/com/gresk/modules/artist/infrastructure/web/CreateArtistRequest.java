package com.gresk.modules.artist.infrastructure.web;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

import java.util.List;

public record CreateArtistRequest(
        @NotBlank @Size(max = 60)   String name,
        @Size(max = 100)            String origin,
        @NotEmpty                   List<String> genres,
                                    String imageUrl,
        @NotBlank @Size(max = 600)  String bio,
        @NotBlank                   String status,
                                    String fee,
                                    String followers,
        @NotBlank                   String contact,
                                    String socialSpotify,
                                    String socialInstagram,
                                    List<String> tags
) {}

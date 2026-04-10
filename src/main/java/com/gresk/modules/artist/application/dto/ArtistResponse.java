package com.gresk.modules.artist.application.dto;

import com.gresk.modules.artist.domain.model.Artist;

import java.util.List;

public record ArtistResponse(
        String id,
        String promoterId,
        String name,
        String origin,
        List<String> genres,
        String imageUrl,
        String bio,
        String status,
        String fee,
        String followers,
        String contact,
        String socialSpotify,
        String socialInstagram,
        List<String> tags,
        int eventsPlayed,
        String createdAt
) {
    public static ArtistResponse from(Artist artist) {
        return new ArtistResponse(
                artist.getId().toString(),
                artist.getPromoterId().toString(),
                artist.getName(),
                artist.getOrigin(),
                artist.getGenres(),
                artist.getImageUrl(),
                artist.getBio(),
                artist.getStatus().name(),
                artist.getFee(),
                artist.getFollowers(),
                artist.getContact(),
                artist.getSocialSpotify(),
                artist.getSocialInstagram(),
                artist.getTags(),
                artist.getEventsPlayed(),
                artist.getCreatedAt().toString()
        );
    }
}

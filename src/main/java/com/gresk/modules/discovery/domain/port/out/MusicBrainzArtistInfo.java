package com.gresk.modules.discovery.domain.port.out;

public record MusicBrainzArtistInfo(
        String musicBrainzId,
        String country,
        String city,
        Integer beginYear
) {
}

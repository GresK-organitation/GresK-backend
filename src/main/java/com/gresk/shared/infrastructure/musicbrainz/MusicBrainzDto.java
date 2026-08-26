package com.gresk.shared.infrastructure.musicbrainz;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class MusicBrainzDto {

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record ArtistSearchResponse(List<ArtistResult> artists) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record ArtistResult(
            String id,
            String name,
            String country,
            Area area,
            @JsonProperty("life-span") LifeSpan lifeSpan
    ) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Area(String name) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record LifeSpan(String begin) {}
}

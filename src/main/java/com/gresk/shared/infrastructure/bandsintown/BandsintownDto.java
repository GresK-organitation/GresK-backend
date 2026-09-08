package com.gresk.shared.infrastructure.bandsintown;

public class BandsintownDto {
    public record ArtistResponse(
            String id,
            String name,
            String url,
            String image_url,
            String tracker_count,          // seguidores Bandsintown
            String upcoming_event_count
    ) {}
}

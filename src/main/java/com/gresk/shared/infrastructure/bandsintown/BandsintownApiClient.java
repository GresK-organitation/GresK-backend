package com.gresk.shared.infrastructure.bandsintown;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "bandsintown-api", url = "${bandsintown.api-url}")
public interface BandsintownApiClient {

    @GetMapping("/artists/{artistName}")
    BandsintownDto.ArtistResponse getArtist(
            @PathVariable("artistName") String artistName,
            @RequestParam("app_id") String appId);
}

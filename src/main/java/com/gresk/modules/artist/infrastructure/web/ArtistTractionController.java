package com.gresk.modules.artist.infrastructure.web;

import com.gresk.modules.artist.application.port.in.GetArtistTractionPort;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/artists/{artistId}/traction")
@RequiredArgsConstructor
public class ArtistTractionController {

    private final GetArtistTractionPort getTractionUseCase;
    private final ArtistTractionResponseMapper mapper;

    @GetMapping
    @PreAuthorize("hasRole('PROMOTER')")
    public ResponseEntity<ArtistTractionResponse> getTraction(@PathVariable String artistId) {
        return ResponseEntity.ok(mapper.toResponse(getTractionUseCase.execute(artistId)));
    }
}

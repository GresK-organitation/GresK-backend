package com.gresk.modules.artist.infrastructure.web;

import com.gresk.modules.artist.application.port.in.GetArtistTourHistoryPort;
import com.gresk.modules.artist.domain.port.out.TourPerformanceRecord;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/artists/{artistId}/tour-history")
@RequiredArgsConstructor
public class ArtistTourHistoryController {

    private final GetArtistTourHistoryPort tourHistoryUseCase;

    @GetMapping
    @PreAuthorize("hasRole('PROMOTER')")
    public ResponseEntity<List<TourPerformanceRecord>> getHistory(@PathVariable String artistId) {
        return ResponseEntity.ok(tourHistoryUseCase.execute(artistId));
    }
}

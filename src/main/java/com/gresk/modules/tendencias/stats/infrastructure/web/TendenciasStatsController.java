package com.gresk.modules.tendencias.stats.infrastructure.web;

import com.gresk.modules.tendencias.stats.application.dto.*;
import com.gresk.modules.tendencias.stats.application.query.*;
import com.gresk.modules.tendencias.stats.application.usecase.*;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.List;

@RestController
@RequestMapping("/api/v1/tendencias")
@RequiredArgsConstructor
public class TendenciasStatsController {

    private final GetMostReviewedArtistUseCase getMostReviewedArtist;
    private final GetTopRatedArtistUseCase getTopRatedArtist;
    private final GetMostVisitedVenueUseCase getMostVisitedVenue;
    private final GetTrendingGenreUseCase getTrendingGenre;
    private final GetMostDiscussedEventUseCase getMostDiscussedEvent;
    private final GetHighestSellThroughUseCase getHighestSellThrough;

    @GetMapping("/artists/most-reviewed")
    public ResponseEntity<List<MostReviewedArtistResponse>> mostReviewedArtists(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant to,
            @RequestParam(defaultValue = "10") int limit) {
        return ResponseEntity.ok(getMostReviewedArtist.execute(new MostReviewedArtistQuery(from, to, limit)));
    }

    @GetMapping("/artists/top-rated")
    public ResponseEntity<List<TopRatedArtistResponse>> topRatedArtists(
            @RequestParam(defaultValue = "5") int minReviews,
            @RequestParam(defaultValue = "10") int limit) {
        return ResponseEntity.ok(getTopRatedArtist.execute(new TopRatedArtistQuery(minReviews, limit)));
    }

    @GetMapping("/venues/most-visited")
    public ResponseEntity<List<MostVisitedVenueResponse>> mostVisitedVenues(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant to,
            @RequestParam(defaultValue = "10") int limit) {
        return ResponseEntity.ok(getMostVisitedVenue.execute(new MostVisitedVenueQuery(from, to, limit)));
    }

    @GetMapping("/genres/trending")
    public ResponseEntity<List<TrendingGenreResponse>> trendingGenres(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant currentFrom,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant currentTo,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant previousFrom,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant previousTo,
            @RequestParam(defaultValue = "10") int limit) {
        return ResponseEntity.ok(getTrendingGenre.execute(
                new TrendingGenreQuery(currentFrom, currentTo, previousFrom, previousTo, limit)));
    }

    @GetMapping("/events/most-discussed")
    public ResponseEntity<List<MostDiscussedEventResponse>> mostDiscussedEvents(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant to,
            @RequestParam(defaultValue = "10") int limit) {
        return ResponseEntity.ok(getMostDiscussedEvent.execute(new MostDiscussedEventQuery(from, to, limit)));
    }

    @GetMapping("/events/highest-sell-through")
    public ResponseEntity<List<HighestSellThroughResponse>> highestSellThrough(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant to,
            @RequestParam(defaultValue = "10") int limit) {
        return ResponseEntity.ok(getHighestSellThrough.execute(new HighestSellThroughQuery(from, to, limit)));
    }
}

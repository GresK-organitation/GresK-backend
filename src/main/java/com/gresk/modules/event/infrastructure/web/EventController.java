package com.gresk.modules.event.infrastructure.web;

import com.gresk.modules.artist.domain.model.Artist;
import com.gresk.modules.artist.domain.model.valueobject.ArtistId;
import com.gresk.modules.artist.domain.port.out.ArtistRepositoryPort;
import com.gresk.modules.event.application.dto.EventResponse;
import com.gresk.modules.event.application.dto.EventResponseMapper;
import com.gresk.modules.event.application.dto.PageResponse;
import com.gresk.modules.event.application.query.GetEventQuery;
import com.gresk.modules.event.application.usecase.CreateEventCommand;
import com.gresk.modules.event.application.usecase.CreateEventUseCase;
import com.gresk.modules.event.application.usecase.GetEventUseCase;
import com.gresk.modules.event.application.usecase.GetLastMinuteEventsUseCase;
import com.gresk.modules.event.application.usecase.ListEventsUseCase;
import com.gresk.modules.event.application.usecase.PublishEventUseCase;
import com.gresk.modules.event.domain.model.Event;
import com.gresk.modules.event.domain.model.EventStatus;
import com.gresk.modules.event.domain.port.out.EventFilter;
import com.gresk.shared.domain.MusicGenre;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/api/v1/events")
@RequiredArgsConstructor
public class EventController {

    private final CreateEventUseCase         createUseCase;
    private final PublishEventUseCase        publishUseCase;
    private final GetEventUseCase            getUseCase;
    private final ListEventsUseCase          listUseCase;
    private final GetLastMinuteEventsUseCase getLastMinuteUseCase;
    private final EventResponseMapper        mapper;
    private final ArtistRepositoryPort       artistRepository;

    // ── Helpers ──────────────────────────────────────────────────────────────

    /** Resuelve el artista vinculado al evento y delega en el mapper. */
    private EventResponse toResponse(Event event) {
        Artist artist = null;
        if (event.getArtistId() != null) {
            artist = artistRepository.findById(ArtistId.of(event.getArtistId().toString()))
                    .orElse(null);
        }
        return mapper.toResponse(event, artist);
    }

    // ── POST /api/v1/events ──────────────────────────────────────────────────
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('PROMOTER')")
    public ResponseEntity<EventResponse> create(
            @RequestPart("data") @Valid CreateEventRequest request,
            @RequestPart(value = "coverImage", required = false) MultipartFile coverImage,
            @AuthenticationPrincipal String promoterId) {

        CreateEventCommand command = new CreateEventCommand(
                promoterId,
                request.title(),
                request.genre(),
                request.price(),
                request.currency(),
                request.totalCapacity(),
                request.eventDate(),
                request.revealAt(),
                request.street(),
                request.city(),
                request.country(),
                request.venue(),
                request.latitude(),
                request.longitude(),
                coverImage,
                request.artistId()
        );
        return ResponseEntity.status(201).body(toResponse(createUseCase.execute(command)));
    }

    // ── PUT /api/v1/events/{id}/publish ──────────────────────────────────────
    @PutMapping("/{id}/publish")
    @PreAuthorize("hasRole('PROMOTER')")
    public ResponseEntity<EventResponse> publish(
            @PathVariable String id,
            @AuthenticationPrincipal String promoterId) {
        return ResponseEntity.ok(toResponse(publishUseCase.execute(id, promoterId)));
    }

    // ── GET /api/v1/events/{id} ──────────────────────────────────────────────
    @GetMapping("/{id}")
    public ResponseEntity<EventResponse> getById(@PathVariable String id) {
        return ResponseEntity.ok(toResponse(getUseCase.execute(new GetEventQuery(id))));
    }

    // ── GET /api/v1/events ───────────────────────────────────────────────────
    @GetMapping
    public ResponseEntity<PageResponse<EventResponse>> list(
            @RequestParam(required = false) String     genre,
            @RequestParam(required = false) String     city,
            @RequestParam(required = false) Instant    dateFrom,
            @RequestParam(required = false) Instant    dateTo,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false) String     artistName,
            @RequestParam(defaultValue = "0")  int    page,
            @RequestParam(defaultValue = "20") int    size) {

        EventFilter filter = new EventFilter(
                Optional.ofNullable(genre).map(MusicGenre::valueOf),
                Optional.ofNullable(city),
                Optional.ofNullable(dateFrom),
                Optional.ofNullable(dateTo),
                Optional.ofNullable(minPrice),
                Optional.ofNullable(maxPrice),
                Optional.ofNullable(artistName),
                Optional.of(EventStatus.PUBLISHED)
        );

        PageRequest pageRequest = PageRequest.of(page, size);

        List<EventResponse> content = listUseCase.execute(filter, pageRequest)
                .stream().map(this::toResponse).toList();
        long total = listUseCase.count(filter);

        return ResponseEntity.ok(PageResponse.of(content, total, pageRequest));
    }

    // ── GET /api/v1/events/last-minute ───────────────────────────────────────
    @GetMapping("/last-minute")
    public ResponseEntity<List<EventResponse>> getLastMinute() {
        List<EventResponse> result = getLastMinuteUseCase.execute()
                .stream()
                .map(this::toResponse)
                .toList();
        return ResponseEntity.ok(result);
    }
}

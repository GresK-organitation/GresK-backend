package com.gresk.modules.event.infrastructure.web;

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
import com.gresk.modules.event.domain.model.EventStatus;
import com.gresk.modules.event.domain.port.out.EventFilter;
import com.gresk.modules.promoter.domain.model.valueobject.PromoterId;
import com.gresk.shared.domain.MusicGenre;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

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

    // ── POST /api/v1/events ──────────────────────────────────────────────────
    @PostMapping
    @PreAuthorize("hasRole('PROMOTER')")
    public ResponseEntity<EventResponse> create(
            @Valid @RequestBody CreateEventRequest request,
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
                request.coverImageUrl(),
                request.artistName(),
                request.artistImageUrl()
        );
        return ResponseEntity.status(201).body(mapper.toResponse(createUseCase.execute(command)));
    }

    // ── PUT /api/v1/events/{id}/publish ──────────────────────────────────────
    @PutMapping("/{id}/publish")
    @PreAuthorize("hasRole('PROMOTER')")
    public ResponseEntity<EventResponse> publish(
            @PathVariable String id,
            @AuthenticationPrincipal String promoterId) {
        return ResponseEntity.ok(mapper.toResponse(publishUseCase.execute(id, promoterId)));
    }

    // ── GET /api/v1/events/{id} ──────────────────────────────────────────────
    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<EventResponse> getById(@PathVariable String id) {
        return ResponseEntity.ok(mapper.toResponse(getUseCase.execute(new GetEventQuery(id))));
    }

    // ── GET /api/v1/events ───────────────────────────────────────────────────
    @GetMapping
    @PreAuthorize("isAuthenticated()")
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
                .stream().map(mapper::toResponse).toList();
        long total = listUseCase.count(filter);

        return ResponseEntity.ok(PageResponse.of(content, total, pageRequest));
    }

    // ── GET /api/v1/events/last-minute ───────────────────────────────────────
    @GetMapping("/last-minute")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<EventResponse>> getLastMinute() {
        List<EventResponse> result = getLastMinuteUseCase.execute()
                .stream()
                .map(mapper::toResponse)
                .toList();
        return ResponseEntity.ok(result);
    }
}

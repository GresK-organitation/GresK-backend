package com.gresk.modules.event.infrastructure.web;

import com.gresk.modules.event.application.dto.EventResponse;
import com.gresk.modules.event.application.dto.EventResponseMapper;
import com.gresk.modules.event.application.dto.PageResponse;
import com.gresk.modules.event.application.query.GetEventQuery;
import com.gresk.modules.event.application.query.ListEventsQuery;
import com.gresk.modules.event.application.usecase.CreateEventCommand;
import com.gresk.modules.event.application.usecase.CreateEventUseCase;
import com.gresk.modules.event.application.usecase.GetEventUseCase;
import com.gresk.modules.event.application.usecase.GetLastMinuteEventsUseCase;
import com.gresk.modules.event.application.usecase.ListEventsUseCase;
import com.gresk.modules.event.application.usecase.PublishEventUseCase;
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
    public ResponseEntity<EventResponse> getById(@PathVariable String id) {
        return ResponseEntity.ok(mapper.toResponse(getUseCase.execute(new GetEventQuery(id))));
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

        ListEventsQuery query = new ListEventsQuery(
                genre, city, dateFrom, dateTo, minPrice, maxPrice, artistName, page, size);

        List<EventResponse> content = listUseCase.execute(query)
                .stream().map(mapper::toResponse).toList();
        long total = listUseCase.count(query);

        return ResponseEntity.ok(PageResponse.of(content, total, PageRequest.of(page, size)));
    }

    // ── GET /api/v1/events/last-minute ───────────────────────────────────────
    @GetMapping("/last-minute")
    public ResponseEntity<List<EventResponse>> getLastMinute() {
        List<EventResponse> result = getLastMinuteUseCase.execute()
                .stream()
                .map(mapper::toResponse)
                .toList();
        return ResponseEntity.ok(result);
    }
}

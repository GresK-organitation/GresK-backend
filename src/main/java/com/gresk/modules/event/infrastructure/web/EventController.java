package com.gresk.modules.event.infrastructure.web;

import com.gresk.modules.event.application.dto.EventResponse;
import com.gresk.modules.event.application.dto.EventResponseMapper;
import com.gresk.modules.event.application.dto.PageResponse;
import com.gresk.modules.event.application.query.GetEventQuery;
import com.gresk.modules.event.application.usecase.CreateEventCommand;
import com.gresk.modules.event.application.usecase.CreateEventUseCase;
import com.gresk.modules.event.application.usecase.GetEventUseCase;
import com.gresk.modules.event.application.usecase.ListEventsUseCase;
import com.gresk.modules.event.application.usecase.PublishEventUseCase;
import com.gresk.modules.event.domain.model.EventStatus;
import com.gresk.modules.event.domain.model.Genre;
import com.gresk.modules.event.domain.port.out.EventFilter;
import com.gresk.modules.promoter.domain.valueobject.PromoterId;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/events")
@RequiredArgsConstructor
public class EventController {

    private final CreateEventUseCase  createUseCase;
    private final PublishEventUseCase publishUseCase;
    private final GetEventUseCase     getUseCase;
    private final ListEventsUseCase   listUseCase;
    private final EventResponseMapper mapper;

    @PostMapping
    @PreAuthorize("hasRole('PROMOTER')")
    public ResponseEntity<EventResponse> create(
            @Valid @RequestBody CreateEventRequest request,
            @AuthenticationPrincipal PromoterId promoterId) {
        CreateEventCommand command = new CreateEventCommand(
                promoterId.toString(), request.title(),
                request.genre(), request.price(), request.currency(),
                request.totalCapacity(), request.eventDate(),
                request.city(), request.address(), request.venue(), null);
        return ResponseEntity.status(201).body(mapper.toResponse(createUseCase.execute(command)));
    }

    @PutMapping("/{id}/publish")
    @PreAuthorize("hasRole('PROMOTER')")
    public ResponseEntity<EventResponse> publish(
            @PathVariable String id,
            @AuthenticationPrincipal PromoterId promoterId) {
        return ResponseEntity.ok(mapper.toResponse(publishUseCase.execute(id, promoterId.toString())));
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<EventResponse> getById(@PathVariable String id) {
        return ResponseEntity.ok(mapper.toResponse(getUseCase.execute(new GetEventQuery(id))));
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<PageResponse<EventResponse>> list(
            @RequestParam(required = false) String genre,
            @RequestParam(required = false) String city,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFrom,
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "20") int size) {

        EventFilter filter = new EventFilter(
                Optional.ofNullable(genre).map(Genre::valueOf),
                Optional.ofNullable(city),
                Optional.ofNullable(dateFrom),
                Optional.empty(),
                Optional.of(EventStatus.PUBLISHED));

        PageRequest pageRequest = PageRequest.of(page, size);

        List<EventResponse> content = listUseCase.execute(filter, pageRequest)
                .stream().map(mapper::toResponse).toList();
        long total = listUseCase.count(filter);

        return ResponseEntity.ok(PageResponse.of(content, total, pageRequest));
    }
}

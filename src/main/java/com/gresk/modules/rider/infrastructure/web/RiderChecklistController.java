package com.gresk.modules.rider.infrastructure.web;

import com.gresk.modules.rider.application.command.ConfirmChecklistItemCommand;
import com.gresk.modules.rider.application.command.LinkRiderToEventCommand;
import com.gresk.modules.rider.application.usecase.ConfirmChecklistItemUseCase;
import com.gresk.modules.rider.application.usecase.GetEventChecklistUseCase;
import com.gresk.modules.rider.application.usecase.LinkRiderToEventUseCase;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@Slf4j
@RestController
@RequiredArgsConstructor
public class RiderChecklistController {

    private final LinkRiderToEventUseCase    linkUseCase;
    private final GetEventChecklistUseCase   getChecklistUseCase;
    private final ConfirmChecklistItemUseCase confirmUseCase;
    private final RiderResponseMapper        mapper;

    // ── POST /api/v1/events/{eventId}/rider ──────────────────────────────────
    @PostMapping("/api/v1/events/{eventId}/rider")
    @PreAuthorize("hasRole('PROMOTER')")
    public ResponseEntity<ChecklistResponse> linkRiderToEvent(
            @PathVariable String eventId,
            @RequestBody @Valid LinkRiderRequest request,
            @AuthenticationPrincipal String promoterId) {

        var checklist = linkUseCase.execute(
                new LinkRiderToEventCommand(promoterId, eventId, request.riderId()));
        return ResponseEntity
                .created(URI.create("/api/v1/events/" + eventId + "/checklist"))
                .body(mapper.toChecklistResponse(checklist));
    }

    // ── GET /api/v1/events/{eventId}/checklist ───────────────────────────────
    @GetMapping("/api/v1/events/{eventId}/checklist")
    @PreAuthorize("hasRole('PROMOTER')")
    public ResponseEntity<ChecklistResponse> getChecklist(@PathVariable String eventId) {
        return ResponseEntity.ok(mapper.toChecklistResponse(getChecklistUseCase.execute(eventId)));
    }

    // ── PATCH /api/v1/events/{eventId}/checklist/{entryId}/confirm ───────────
    @PatchMapping("/api/v1/events/{eventId}/checklist/{entryId}/confirm")
    @PreAuthorize("hasRole('PROMOTER')")
    public ResponseEntity<ChecklistResponse> confirmEntry(
            @PathVariable String eventId,
            @PathVariable String entryId,
            @RequestBody(required = false) ConfirmChecklistItemRequest request,
            @AuthenticationPrincipal String promoterId) {

        String notes = request != null ? request.confirmedNotes() : null;
        var checklist = confirmUseCase.execute(
                new ConfirmChecklistItemCommand(promoterId, eventId, entryId, notes));
        return ResponseEntity.ok(mapper.toChecklistResponse(checklist));
    }
}

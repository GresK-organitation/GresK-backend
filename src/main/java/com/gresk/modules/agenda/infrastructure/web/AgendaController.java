package com.gresk.modules.agenda.infrastructure.web;

import com.gresk.modules.agenda.application.command.CreateAgendaEntryCommand;
import com.gresk.modules.agenda.application.command.UpdateAgendaEntryCommand;
import com.gresk.modules.agenda.application.dto.AgendaEntryResponse;
import com.gresk.modules.agenda.application.dto.AgendaResponseMapper;
import com.gresk.modules.agenda.application.dto.AgendaViewResponse;
import com.gresk.modules.agenda.application.query.AgendaViewQuery;
import com.gresk.modules.agenda.application.usecase.*;
import com.gresk.modules.agenda.domain.model.EntityReference;
import com.gresk.modules.agenda.domain.model.RecurrenceRule;
import com.gresk.modules.agenda.domain.model.UpdateScope;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.Set;

@RestController
@RequestMapping("/api/v1/agenda")
@RequiredArgsConstructor
@PreAuthorize("hasRole('PROMOTER')")
public class AgendaController {

    private final CreateAgendaEntryUseCase create;
    private final GetAgendaEntryUseCase    getById;
    private final UpdateAgendaEntryUseCase update;
    private final DeleteAgendaEntryUseCase delete;
    private final CompleteTaskUseCase      complete;
    private final GetAgendaViewUseCase     view;
    private final AgendaResponseMapper     mapper;

    // ── POST /api/v1/agenda/entries ──────────────────────────────────────────
    @PostMapping("/entries")
    public ResponseEntity<AgendaEntryResponse> createEntry(
            @RequestBody @Valid CreateAgendaEntryRequest req,
            @AuthenticationPrincipal String promoterId) {

        RecurrenceRule rule = buildRecurrenceRule(req);
        EntityReference linked = buildLinkedEntity(req.linkedEntityType(), req.linkedEntityId());

        CreateAgendaEntryCommand cmd = new CreateAgendaEntryCommand(
                promoterId, req.type(), req.title(), req.description(),
                req.startAt(), req.endAt(), req.allDay(),
                req.color(), req.label(), linked, rule,
                req.reminderMinutesBefore()
        );
        return ResponseEntity.status(201).body(mapper.toResponse(create.execute(cmd)));
    }

    // ── GET /api/v1/agenda/entries/{id} ──────────────────────────────────────
    @GetMapping("/entries/{id}")
    public ResponseEntity<AgendaEntryResponse> getEntry(
            @PathVariable String id,
            @AuthenticationPrincipal String promoterId) {
        return ResponseEntity.ok(mapper.toResponse(getById.execute(id, promoterId)));
    }

    // ── PUT /api/v1/agenda/entries/{id} ──────────────────────────────────────
    @PutMapping("/entries/{id}")
    public ResponseEntity<AgendaEntryResponse> updateEntry(
            @PathVariable String id,
            @RequestParam(defaultValue = "ALL") UpdateScope scope,
            @RequestParam(required = false) Instant occurrenceDate,
            @RequestBody @Valid UpdateAgendaEntryRequest req,
            @AuthenticationPrincipal String promoterId) {

        EntityReference linked = buildLinkedEntity(req.linkedEntityType(), req.linkedEntityId());

        UpdateAgendaEntryCommand cmd = new UpdateAgendaEntryCommand(
                id, promoterId, scope, occurrenceDate,
                req.title(), req.description(),
                req.startAt(), req.endAt(), req.allDay(),
                req.color(), req.label(), linked,
                req.reminderMinutesBefore()
        );
        return ResponseEntity.ok(mapper.toResponse(update.execute(cmd)));
    }

    // ── DELETE /api/v1/agenda/entries/{id} ───────────────────────────────────
    @DeleteMapping("/entries/{id}")
    public ResponseEntity<Void> deleteEntry(
            @PathVariable String id,
            @RequestParam(defaultValue = "ALL") UpdateScope scope,
            @RequestParam(required = false) Instant occurrenceDate,
            @AuthenticationPrincipal String promoterId) {
        delete.execute(id, promoterId, scope, occurrenceDate);
        return ResponseEntity.noContent().build();
    }

    // ── PATCH /api/v1/agenda/entries/{id}/complete ───────────────────────────
    @PatchMapping("/entries/{id}/complete")
    public ResponseEntity<AgendaEntryResponse> completeTask(
            @PathVariable String id,
            @AuthenticationPrincipal String promoterId) {
        return ResponseEntity.ok(mapper.toResponse(complete.execute(id, promoterId)));
    }

    // ── GET /api/v1/agenda/view ──────────────────────────────────────────────
    @GetMapping("/view")
    public ResponseEntity<AgendaViewResponse> getView(
            @AuthenticationPrincipal String promoterId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            @RequestParam(required = false) Set<String> types) {

        Instant fromInstant = from.atStartOfDay(ZoneOffset.UTC).toInstant();
        Instant toInstant   = to.plusDays(1).atStartOfDay(ZoneOffset.UTC).toInstant();

        AgendaViewQuery query = new AgendaViewQuery(promoterId, fromInstant, toInstant, types);
        return ResponseEntity.ok(view.execute(query));
    }

    // ── Helpers ──────────────────────────────────────────────────────────────

    private RecurrenceRule buildRecurrenceRule(CreateAgendaEntryRequest req) {
        if (req.recurrenceFrequency() == null) return null;
        return new RecurrenceRule(
                req.recurrenceFrequency(),
                req.recurrenceInterval() != null ? req.recurrenceInterval() : 1,
                req.recurrenceCount(),
                req.recurrenceUntil(),
                req.recurrenceByDay() != null ? req.recurrenceByDay() : Set.of(),
                req.recurrenceByMonthDay()
        );
    }

    private EntityReference buildLinkedEntity(
            com.gresk.modules.agenda.domain.model.LinkedEntityType type,
            java.util.UUID id) {
        if (type == null || id == null) return null;
        return new EntityReference(type, id);
    }
}

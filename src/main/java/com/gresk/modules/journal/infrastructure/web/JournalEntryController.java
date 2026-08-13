package com.gresk.modules.journal.infrastructure.web;

import com.gresk.modules.journal.application.command.CreateJournalEntryCommand;
import com.gresk.modules.journal.application.command.RatingCriterionInput;
import com.gresk.modules.journal.application.command.UpdateJournalEntryCommand;
import com.gresk.modules.journal.application.dto.BulkCreateResult;
import com.gresk.modules.journal.application.port.in.CreateJournalEntryPort;
import com.gresk.modules.journal.application.port.in.DeleteJournalEntryPort;
import com.gresk.modules.journal.application.port.in.UpdateJournalEntryPort;
import com.gresk.modules.journal.application.query.ListMyJournalEntriesQuery;
import com.gresk.modules.journal.application.usecase.AddJournalMediaUseCase;
import com.gresk.modules.journal.application.usecase.BulkCreateJournalEntriesUseCase;
import com.gresk.modules.journal.application.usecase.GetJournalEntryUseCase;
import com.gresk.modules.journal.application.usecase.GetSuggestedRatingCriteriaUseCase;
import com.gresk.modules.journal.application.usecase.ListMyJournalEntriesUseCase;
import com.gresk.modules.journal.application.usecase.RemoveJournalMediaUseCase;
import com.gresk.modules.journal.domain.model.JournalEntry;
import com.gresk.modules.journal.domain.model.JournalEntrySource;
import com.gresk.modules.journal.domain.model.JournalMediaType;
import com.gresk.modules.journal.domain.model.JournalVisibility;
import com.gresk.modules.journal.infrastructure.web.request.BulkCreateJournalEntriesRequest;
import com.gresk.modules.journal.infrastructure.web.request.CreateJournalEntryRequest;
import com.gresk.modules.journal.infrastructure.web.request.RatingCriterionRequest;
import com.gresk.modules.journal.infrastructure.web.request.UpdateJournalEntryRequest;
import com.gresk.shared.application.dto.PageResponse;
import com.gresk.shared.domain.MusicGenre;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/journal")
@RequiredArgsConstructor
@Tag(name = "Journal", description = "Free-form music diary entries — no ticket or catalog event required")
public class JournalEntryController {

    private final CreateJournalEntryPort              createJournalEntryPort;
    private final UpdateJournalEntryPort              updateJournalEntryPort;
    private final DeleteJournalEntryPort              deleteJournalEntryPort;
    private final GetJournalEntryUseCase              getJournalEntryUseCase;
    private final ListMyJournalEntriesUseCase         listMyJournalEntriesUseCase;
    private final BulkCreateJournalEntriesUseCase     bulkCreateJournalEntriesUseCase;
    private final AddJournalMediaUseCase              addJournalMediaUseCase;
    private final RemoveJournalMediaUseCase           removeJournalMediaUseCase;
    private final GetSuggestedRatingCriteriaUseCase   getSuggestedRatingCriteriaUseCase;
    private final JournalEntryResponseMapper          mapper;

    // ── POST /api/v1/journal/entries ─────────────────────────────────────────

    @PostMapping("/entries")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Log a free-form music experience — no ticket or catalog event required")
    public ResponseEntity<JournalEntryResponse> create(
            @Valid @RequestBody CreateJournalEntryRequest request,
            @AuthenticationPrincipal String userId) {

        JournalEntry entry = createJournalEntryPort.execute(
                toCommand(request, userId, JournalEntrySource.MANUAL));

        return ResponseEntity.status(201).body(mapper.toResponse(entry));
    }

    // ── POST /api/v1/journal/entries/bulk ────────────────────────────────────

    @PostMapping("/entries/bulk")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Retroactively import past experiences in one request (partial success)")
    public ResponseEntity<BulkCreateResultResponse> bulkCreate(
            @Valid @RequestBody BulkCreateJournalEntriesRequest request,
            @AuthenticationPrincipal String userId) {

        List<CreateJournalEntryCommand> commands = request.entries().stream()
                .map(r -> toCommand(r, userId, JournalEntrySource.BULK_IMPORT))
                .toList();

        BulkCreateResult result = bulkCreateJournalEntriesUseCase.execute(commands);

        List<JournalEntryResponse> created = result.created().stream().map(mapper::toResponse).toList();
        List<BulkCreateResultResponse.BulkCreateFailureResponse> failures = result.failures().stream()
                .map(f -> new BulkCreateResultResponse.BulkCreateFailureResponse(f.index(), f.reason()))
                .toList();

        return ResponseEntity.status(201).body(new BulkCreateResultResponse(created, failures));
    }

    // ── PUT /api/v1/journal/entries/{id} ─────────────────────────────────────

    @PutMapping("/entries/{id}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Edit an existing journal entry")
    public ResponseEntity<JournalEntryResponse> update(
            @PathVariable String id,
            @Valid @RequestBody UpdateJournalEntryRequest request,
            @AuthenticationPrincipal String userId) {

        UpdateJournalEntryCommand command = new UpdateJournalEntryCommand(
                id, userId, request.artistName(), request.artistId(),
                request.date(), request.datePrecision(), request.venueName(), request.city(),
                request.eventId(), request.notes(), toCriteriaInputs(request.criteria()),
                request.genre() != null ? MusicGenre.valueOf(request.genre()) : null,
                JournalVisibility.valueOf(request.visibility())
        );

        return ResponseEntity.ok(mapper.toResponse(updateJournalEntryPort.execute(command)));
    }

    // ── DELETE /api/v1/journal/entries/{id} ──────────────────────────────────

    @DeleteMapping("/entries/{id}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Delete a journal entry")
    public ResponseEntity<Void> delete(@PathVariable String id, @AuthenticationPrincipal String userId) {
        deleteJournalEntryPort.execute(id, userId);
        return ResponseEntity.noContent().build();
    }

    // ── GET /api/v1/journal/entries/{id} ─────────────────────────────────────

    @GetMapping("/entries/{id}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get a journal entry (owner, or public entries)")
    public ResponseEntity<JournalEntryResponse> getById(
            @PathVariable String id, @AuthenticationPrincipal String userId) {

        return ResponseEntity.ok(mapper.toResponse(getJournalEntryUseCase.execute(id, userId)));
    }

    // ── GET /api/v1/journal/entries/mine ─────────────────────────────────────

    @GetMapping("/entries/mine")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "List the authenticated user's journal entries")
    public ResponseEntity<PageResponse<JournalEntryResponse>> listMine(
            @RequestParam(required = false) String    artistId,
            @RequestParam(required = false) String    genre,
            @RequestParam(required = false) LocalDate dateFrom,
            @RequestParam(required = false) LocalDate dateTo,
            @RequestParam(defaultValue = "0")  int     page,
            @RequestParam(defaultValue = "20") int     size,
            @AuthenticationPrincipal String userId) {

        ListMyJournalEntriesQuery query = new ListMyJournalEntriesQuery(
                userId, artistId, genre != null ? MusicGenre.valueOf(genre) : null,
                dateFrom, dateTo, page, size);

        List<JournalEntryResponse> content = listMyJournalEntriesUseCase.execute(query)
                .stream().map(mapper::toResponse).toList();
        long total = listMyJournalEntriesUseCase.count(query);

        return ResponseEntity.ok(PageResponse.of(content, total, PageRequest.of(page, size)));
    }

    // ── POST /api/v1/journal/entries/{id}/media ──────────────────────────────

    @PostMapping(value = "/entries/{id}/media", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Attach a photo or a short video clip (max 15s) to an entry")
    public ResponseEntity<JournalEntryResponse> addMedia(
            @PathVariable String id,
            @RequestParam("mediaType") JournalMediaType mediaType,
            @RequestParam("file") MultipartFile file,
            @AuthenticationPrincipal String userId) {

        JournalEntry entry = addJournalMediaUseCase.execute(id, userId, mediaType, file);
        return ResponseEntity.ok(mapper.toResponse(entry));
    }

    // ── DELETE /api/v1/journal/entries/{id}/media/{mediaId} ──────────────────

    @DeleteMapping("/entries/{id}/media/{mediaId}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Remove a media attachment from an entry")
    public ResponseEntity<JournalEntryResponse> removeMedia(
            @PathVariable String id,
            @PathVariable String mediaId,
            @AuthenticationPrincipal String userId) {

        JournalEntry entry = removeJournalMediaUseCase.execute(id, userId, mediaId);
        return ResponseEntity.ok(mapper.toResponse(entry));
    }

    // ── GET /api/v1/journal/rating-templates ─────────────────────────────────

    @GetMapping("/rating-templates")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Suggested rating criteria for a genre, used to prefill the client form")
    public ResponseEntity<SuggestedCriteriaResponse> suggestedCriteria(
            @RequestParam(required = false) String genre) {

        MusicGenre parsedGenre = genre != null ? MusicGenre.valueOf(genre) : null;
        return ResponseEntity.ok(new SuggestedCriteriaResponse(getSuggestedRatingCriteriaUseCase.execute(parsedGenre)));
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private CreateJournalEntryCommand toCommand(CreateJournalEntryRequest request, String userId, JournalEntrySource source) {
        return new CreateJournalEntryCommand(
                userId, request.artistName(), request.artistId(),
                request.date(), request.datePrecision(), request.venueName(), request.city(),
                request.eventId(), request.notes(), toCriteriaInputs(request.criteria()),
                request.genre() != null ? MusicGenre.valueOf(request.genre()) : null,
                request.visibility() != null ? JournalVisibility.valueOf(request.visibility()) : JournalVisibility.PRIVATE,
                source
        );
    }

    private List<RatingCriterionInput> toCriteriaInputs(List<RatingCriterionRequest> criteria) {
        if (criteria == null) return List.of();
        return criteria.stream().map(c -> new RatingCriterionInput(c.label(), c.value())).toList();
    }
}

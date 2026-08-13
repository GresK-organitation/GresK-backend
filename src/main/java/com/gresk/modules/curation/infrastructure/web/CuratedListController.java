package com.gresk.modules.curation.infrastructure.web;

import com.gresk.modules.curation.application.command.AddListItemCommand;
import com.gresk.modules.curation.application.command.CreateCuratedListCommand;
import com.gresk.modules.curation.application.command.UpdateCuratedListCommand;
import com.gresk.modules.curation.application.port.in.AddListItemPort;
import com.gresk.modules.curation.application.port.in.CreateCuratedListPort;
import com.gresk.modules.curation.application.port.in.DeleteCuratedListPort;
import com.gresk.modules.curation.application.port.in.UpdateCuratedListPort;
import com.gresk.modules.curation.application.query.DiscoverCuratedListsQuery;
import com.gresk.modules.curation.application.query.ListMyCuratedListsQuery;
import com.gresk.modules.curation.application.usecase.*;
import com.gresk.modules.curation.domain.model.CuratedList;
import com.gresk.modules.curation.infrastructure.web.request.AddListItemRequest;
import com.gresk.modules.curation.infrastructure.web.request.CreateCuratedListRequest;
import com.gresk.modules.curation.infrastructure.web.request.ReorderListItemsRequest;
import com.gresk.modules.curation.infrastructure.web.request.UpdateCuratedListRequest;
import com.gresk.modules.user.domain.model.UserId;
import com.gresk.shared.application.dto.PageResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Tag(name = "Curated lists", description = "Themed, shareable and followable collections of reviews and journal entries")
public class CuratedListController {

    private final CreateCuratedListPort         createCuratedListPort;
    private final UpdateCuratedListPort         updateCuratedListPort;
    private final DeleteCuratedListPort         deleteCuratedListPort;
    private final GetCuratedListUseCase         getCuratedListUseCase;
    private final ListMyCuratedListsUseCase     listMyCuratedListsUseCase;
    private final DiscoverPublicCuratedListsUseCase discoverPublicCuratedListsUseCase;
    private final AddListItemPort               addListItemPort;
    private final RemoveListItemUseCase         removeListItemUseCase;
    private final ReorderListItemsUseCase       reorderListItemsUseCase;
    private final FollowListUseCase             followListUseCase;
    private final UnfollowListUseCase           unfollowListUseCase;
    private final ListFollowersUseCase          listFollowersUseCase;
    private final ListFollowedListsUseCase      listFollowedListsUseCase;
    private final CuratedListResponseMapper     mapper;

    // ── POST /api/v1/lists ────────────────────────────────────────────────────

    @PostMapping("/api/v1/lists")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Create a curated list")
    public ResponseEntity<CuratedListResponse> create(
            @Valid @RequestBody CreateCuratedListRequest request,
            @AuthenticationPrincipal String userId) {

        CuratedList list = createCuratedListPort.execute(new CreateCuratedListCommand(
                userId, request.title(), request.description(), request.visibility()));
        return ResponseEntity.status(201).body(mapper.toResponse(list));
    }

    // ── PUT /api/v1/lists/{id} ────────────────────────────────────────────────

    @PutMapping("/api/v1/lists/{id}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Edit a curated list's title, description or visibility")
    public ResponseEntity<CuratedListResponse> update(
            @PathVariable String id,
            @Valid @RequestBody UpdateCuratedListRequest request,
            @AuthenticationPrincipal String userId) {

        CuratedList list = updateCuratedListPort.execute(new UpdateCuratedListCommand(
                id, userId, request.title(), request.description(), request.visibility()));
        return ResponseEntity.ok(mapper.toResponse(list));
    }

    // ── DELETE /api/v1/lists/{id} ─────────────────────────────────────────────

    @DeleteMapping("/api/v1/lists/{id}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Delete a curated list")
    public ResponseEntity<Void> delete(@PathVariable String id, @AuthenticationPrincipal String userId) {
        deleteCuratedListPort.execute(id, userId);
        return ResponseEntity.noContent().build();
    }

    // ── GET /api/v1/lists/{id} ────────────────────────────────────────────────

    @GetMapping("/api/v1/lists/{id}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get a curated list (owner, or public lists)")
    public ResponseEntity<CuratedListResponse> getById(
            @PathVariable String id, @AuthenticationPrincipal String userId) {

        return ResponseEntity.ok(mapper.toResponse(getCuratedListUseCase.execute(id, userId)));
    }

    // ── GET /api/v1/lists/mine ────────────────────────────────────────────────

    @GetMapping("/api/v1/lists/mine")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "List the authenticated user's curated lists")
    public ResponseEntity<PageResponse<CuratedListResponse>> listMine(
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "20") int size,
            @AuthenticationPrincipal String userId) {

        ListMyCuratedListsQuery query = new ListMyCuratedListsQuery(userId, page, size);
        List<CuratedListResponse> content = listMyCuratedListsUseCase.execute(query)
                .stream().map(mapper::toResponse).toList();
        long total = listMyCuratedListsUseCase.count(query);

        return ResponseEntity.ok(PageResponse.of(content, total, PageRequest.of(page, size)));
    }

    // ── GET /api/v1/lists/discover ────────────────────────────────────────────

    @GetMapping("/api/v1/lists/discover")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Discover public curated lists")
    public ResponseEntity<PageResponse<CuratedListResponse>> discover(
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "20") int size) {

        DiscoverCuratedListsQuery query = new DiscoverCuratedListsQuery(page, size);
        List<CuratedListResponse> content = discoverPublicCuratedListsUseCase.execute(query)
                .stream().map(mapper::toResponse).toList();
        long total = discoverPublicCuratedListsUseCase.count(query);

        return ResponseEntity.ok(PageResponse.of(content, total, PageRequest.of(page, size)));
    }

    // ── POST /api/v1/lists/{id}/items ─────────────────────────────────────────

    @PostMapping("/api/v1/lists/{id}/items")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Add a review or journal entry to a list")
    public ResponseEntity<CuratedListResponse> addItem(
            @PathVariable String id,
            @Valid @RequestBody AddListItemRequest request,
            @AuthenticationPrincipal String userId) {

        CuratedList list = addListItemPort.execute(new AddListItemCommand(
                id, userId, request.entryType(), request.entryId()));
        return ResponseEntity.status(201).body(mapper.toResponse(list));
    }

    // ── DELETE /api/v1/lists/{id}/items/{itemId} ──────────────────────────────

    @DeleteMapping("/api/v1/lists/{id}/items/{itemId}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Remove an item from a list")
    public ResponseEntity<CuratedListResponse> removeItem(
            @PathVariable String id,
            @PathVariable String itemId,
            @AuthenticationPrincipal String userId) {

        return ResponseEntity.ok(mapper.toResponse(removeListItemUseCase.execute(id, userId, itemId)));
    }

    // ── PUT /api/v1/lists/{id}/items/reorder ──────────────────────────────────

    @PutMapping("/api/v1/lists/{id}/items/reorder")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Reorder the items of a list")
    public ResponseEntity<CuratedListResponse> reorder(
            @PathVariable String id,
            @Valid @RequestBody ReorderListItemsRequest request,
            @AuthenticationPrincipal String userId) {

        CuratedList list = reorderListItemsUseCase.execute(id, userId, request.itemIds());
        return ResponseEntity.ok(mapper.toResponse(list));
    }

    // ── POST /api/v1/lists/{id}/follow ────────────────────────────────────────

    @PostMapping("/api/v1/lists/{id}/follow")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Follow a public curated list")
    public ResponseEntity<Void> follow(@PathVariable String id, @AuthenticationPrincipal String userId) {
        followListUseCase.execute(id, userId);
        return ResponseEntity.ok().build();
    }

    // ── DELETE /api/v1/lists/{id}/follow ──────────────────────────────────────

    @DeleteMapping("/api/v1/lists/{id}/follow")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Unfollow a curated list")
    public ResponseEntity<Void> unfollow(@PathVariable String id, @AuthenticationPrincipal String userId) {
        unfollowListUseCase.execute(id, userId);
        return ResponseEntity.ok().build();
    }

    // ── GET /api/v1/lists/{id}/followers ──────────────────────────────────────

    @GetMapping("/api/v1/lists/{id}/followers")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "List the followers of a curated list")
    public ResponseEntity<PageResponse<String>> followers(
            @PathVariable String id,
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "20") int size,
            @AuthenticationPrincipal String userId) {

        List<String> content = listFollowersUseCase.execute(id, userId, page, size).stream()
                .map(UserId::toString).toList();
        long total = listFollowersUseCase.count(id, userId);

        return ResponseEntity.ok(PageResponse.of(content, total, PageRequest.of(page, size)));
    }

    // ── GET /api/v1/users/me/following-lists ──────────────────────────────────

    @GetMapping("/api/v1/users/me/following-lists")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "List the curated lists the authenticated user follows")
    public ResponseEntity<PageResponse<CuratedListResponse>> followingLists(
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "20") int size,
            @AuthenticationPrincipal String userId) {

        List<CuratedListResponse> content = listFollowedListsUseCase.execute(userId, page, size)
                .stream().map(mapper::toResponse).toList();
        long total = listFollowedListsUseCase.count(userId);

        return ResponseEntity.ok(PageResponse.of(content, total, PageRequest.of(page, size)));
    }
}

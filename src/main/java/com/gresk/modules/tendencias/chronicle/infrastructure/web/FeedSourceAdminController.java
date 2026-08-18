package com.gresk.modules.tendencias.chronicle.infrastructure.web;

import com.gresk.modules.tendencias.chronicle.application.command.RegisterFeedSourceCommand;
import com.gresk.modules.tendencias.chronicle.application.dto.ChronicleResponseMapper;
import com.gresk.modules.tendencias.chronicle.application.dto.FeedSourceResponse;
import com.gresk.modules.tendencias.chronicle.application.usecase.*;
import com.gresk.modules.tendencias.chronicle.domain.model.ChronicleId;
import com.gresk.modules.tendencias.chronicle.domain.model.FeedSourceId;
import com.gresk.modules.tendencias.chronicle.infrastructure.web.request.RegisterFeedSourceRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/tendencias/admin")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class FeedSourceAdminController {

    private final RegisterFeedSourceUseCase registerFeedSource;
    private final ApproveFeedSourceUseCase approveFeedSource;
    private final RejectFeedSourceUseCase rejectFeedSource;
    private final ListFeedSourcesUseCase listFeedSources;
    private final HideChronicleUseCase hideChronicle;

    @PostMapping("/feed-sources")
    public ResponseEntity<FeedSourceResponse> register(@RequestBody @Valid RegisterFeedSourceRequest request) {
        var feedSource = registerFeedSource.execute(
                new RegisterFeedSourceCommand(request.name(), request.feedUrl(), request.sourceUrl()));
        return ResponseEntity.status(HttpStatus.CREATED).body(ChronicleResponseMapper.toResponse(feedSource));
    }

    @GetMapping("/feed-sources")
    public ResponseEntity<List<FeedSourceResponse>> list() {
        var feedSources = listFeedSources.execute().stream().map(ChronicleResponseMapper::toResponse).toList();
        return ResponseEntity.ok(feedSources);
    }

    @PutMapping("/feed-sources/{id}/approve")
    public ResponseEntity<FeedSourceResponse> approve(@PathVariable UUID id,
                                                        @AuthenticationPrincipal String adminId) {
        var feedSource = approveFeedSource.execute(FeedSourceId.of(id), UUID.fromString(adminId));
        return ResponseEntity.ok(ChronicleResponseMapper.toResponse(feedSource));
    }

    @PutMapping("/feed-sources/{id}/reject")
    public ResponseEntity<FeedSourceResponse> reject(@PathVariable UUID id,
                                                       @AuthenticationPrincipal String adminId) {
        var feedSource = rejectFeedSource.execute(FeedSourceId.of(id), UUID.fromString(adminId));
        return ResponseEntity.ok(ChronicleResponseMapper.toResponse(feedSource));
    }

    @PutMapping("/chronicles/{id}/hide")
    public ResponseEntity<Void> hide(@PathVariable UUID id) {
        hideChronicle.execute(ChronicleId.of(id));
        return ResponseEntity.noContent().build();
    }
}

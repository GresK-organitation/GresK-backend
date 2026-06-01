package com.gresk.modules.rider.infrastructure.web;

import com.gresk.modules.rider.application.usecase.GetPendingRidersUseCase;
import com.gresk.modules.rider.application.usecase.GetRiderAlertsUseCase;
import com.gresk.modules.rider.application.usecase.MarkAlertReadUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/promoter")
@RequiredArgsConstructor
public class RiderAlertController {

    private final GetRiderAlertsUseCase  getAlertsUseCase;
    private final MarkAlertReadUseCase   markReadUseCase;
    private final GetPendingRidersUseCase pendingRidersUseCase;
    private final RiderResponseMapper    mapper;

    // ── GET /api/v1/promoter/rider-alerts ────────────────────────────────────
    @GetMapping("/rider-alerts")
    @PreAuthorize("hasRole('PROMOTER')")
    public ResponseEntity<List<RiderAlertResponse>> getAlerts(
            @AuthenticationPrincipal String promoterId) {

        List<RiderAlertResponse> alerts = getAlertsUseCase.execute(promoterId).stream()
                .map(mapper::toAlertResponse)
                .toList();
        return ResponseEntity.ok(alerts);
    }

    // ── PATCH /api/v1/promoter/rider-alerts/{id}/read ────────────────────────
    @PatchMapping("/rider-alerts/{id}/read")
    @PreAuthorize("hasRole('PROMOTER')")
    public ResponseEntity<RiderAlertResponse> markRead(
            @PathVariable String id,
            @AuthenticationPrincipal String promoterId) {

        return ResponseEntity.ok(mapper.toAlertResponse(markReadUseCase.execute(id, promoterId)));
    }

    // ── GET /api/v1/promoter/pending-riders ──────────────────────────────────
    @GetMapping("/pending-riders")
    @PreAuthorize("hasRole('PROMOTER')")
    public ResponseEntity<List<PendingRiderResponse>> getPendingRiders(
            @AuthenticationPrincipal String promoterId) {

        List<PendingRiderResponse> pending = pendingRidersUseCase.execute(promoterId).stream()
                .map(mapper::toPendingResponse)
                .toList();
        return ResponseEntity.ok(pending);
    }
}

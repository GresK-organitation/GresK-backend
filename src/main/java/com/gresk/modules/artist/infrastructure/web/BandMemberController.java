package com.gresk.modules.artist.infrastructure.web;

import com.gresk.modules.artist.application.command.AddBandMemberCommand;
import com.gresk.modules.artist.application.command.UpdateBandMemberDocumentCommand;
import com.gresk.modules.artist.application.port.in.*;
import com.gresk.modules.artist.domain.model.BandMember;
import com.gresk.modules.artist.domain.model.DocumentExpiryAlert;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class BandMemberController {

    private final AddBandMemberPort               addUseCase;
    private final UpdateBandMemberDocumentPort    updateDocumentUseCase;
    private final ListBandMembersPort             listUseCase;
    private final ListDocumentExpiryAlertsPort    listAlertsUseCase;
    private final MarkDocumentExpiryAlertReadPort markAlertReadUseCase;
    private final BandMemberResponseMapper        mapper;

    @PostMapping("/api/v1/artists/{artistId}/band-members")
    @PreAuthorize("hasRole('PROMOTER')")
    public ResponseEntity<BandMemberResponse> add(
            @PathVariable String artistId,
            @RequestBody @Valid AddBandMemberRequest request,
            @AuthenticationPrincipal String promoterId) {

        BandMember member = addUseCase.execute(new AddBandMemberCommand(
                artistId, promoterId, request.name(), request.roleInBand()));

        return ResponseEntity
                .created(URI.create("/api/v1/artists/" + artistId + "/band-members/" + member.getId().value()))
                .body(mapper.toResponse(member));
    }

    @GetMapping("/api/v1/artists/{artistId}/band-members")
    @PreAuthorize("hasRole('PROMOTER')")
    public ResponseEntity<List<BandMemberResponse>> list(
            @PathVariable String artistId,
            @AuthenticationPrincipal String promoterId) {
        return ResponseEntity.ok(listUseCase.execute(artistId, promoterId).stream()
                .map(mapper::toResponse).toList());
    }

    @PutMapping("/api/v1/artists/{artistId}/band-members/{bandMemberId}/documents")
    @PreAuthorize("hasRole('PROMOTER')")
    public ResponseEntity<BandMemberResponse> upsertDocument(
            @PathVariable String artistId,
            @PathVariable String bandMemberId,
            @RequestBody @Valid UpdateBandMemberDocumentRequest request,
            @AuthenticationPrincipal String promoterId) {

        BandMember member = updateDocumentUseCase.execute(new UpdateBandMemberDocumentCommand(
                bandMemberId, promoterId, request.documentType(), request.documentNumber(),
                request.issuingCountry(), request.expiryDate()));

        return ResponseEntity.ok(mapper.toResponse(member));
    }

    @GetMapping("/api/v1/promoter/document-expiry-alerts")
    @PreAuthorize("hasRole('PROMOTER')")
    public ResponseEntity<List<DocumentExpiryAlertResponse>> listAlerts(
            @AuthenticationPrincipal String promoterId) {
        List<DocumentExpiryAlertResponse> alerts = listAlertsUseCase.execute(promoterId).stream()
                .map(mapper::toAlertResponse).toList();
        return ResponseEntity.ok(alerts);
    }

    @PatchMapping("/api/v1/promoter/document-expiry-alerts/{alertId}/read")
    @PreAuthorize("hasRole('PROMOTER')")
    public ResponseEntity<DocumentExpiryAlertResponse> markAlertRead(
            @PathVariable String alertId,
            @AuthenticationPrincipal String promoterId) {
        DocumentExpiryAlert alert = markAlertReadUseCase.execute(alertId, promoterId);
        return ResponseEntity.ok(mapper.toAlertResponse(alert));
    }
}

package com.gresk.modules.rider.infrastructure.web;

import com.gresk.modules.rider.application.command.AddLineItemCommand;
import com.gresk.modules.rider.application.command.CreateRiderCommand;
import com.gresk.modules.rider.application.command.ProposeEquipmentSubstitutionCommand;
import com.gresk.modules.rider.application.command.UpdateRiderCommand;
import com.gresk.modules.rider.application.usecase.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/v1/riders")
@RequiredArgsConstructor
public class RiderController {

    private final CreateRiderUseCase             createUseCase;
    private final GetRiderUseCase                getUseCase;
    private final UpdateRiderUseCase             updateUseCase;
    private final PublishRiderUseCase            publishUseCase;
    private final GenerateRiderPdfUseCase        pdfUseCase;
    private final CloneRiderUseCase              cloneUseCase;
    private final GenerateShareLinkUseCase       shareLinkUseCase;
    private final GetPublicRiderUseCase          publicRiderUseCase;
    private final CreateRiderFromTemplateUseCase fromTemplateUseCase;
    private final AddTechnicalLineItemUseCase    addLineItemUseCase;
    private final RemoveTechnicalLineItemUseCase removeLineItemUseCase;
    private final SetTechnicalLineItemFulfillmentUseCase setFulfillmentUseCase;
    private final ProposeEquipmentSubstitutionUseCase proposeSubstitutionUseCase;
    private final DecideEquipmentSubstitutionUseCase decideSubstitutionUseCase;
    private final RiderResponseMapper            mapper;

    // ── POST /api/v1/riders ──────────────────────────────────────────────────
    @PostMapping
    @PreAuthorize("hasRole('PROMOTER')")
    public ResponseEntity<RiderResponse> create(
            @RequestBody @Valid CreateRiderRequest request,
            @AuthenticationPrincipal String promoterId) {

        var rider = createUseCase.execute(
                new CreateRiderCommand(promoterId, request.artistId(), request.name()));
        return ResponseEntity
                .created(URI.create("/api/v1/riders/" + rider.getId()))
                .body(mapper.toResponse(rider));
    }

    // ── GET /api/v1/riders/{riderId} ─────────────────────────────────────────
    @GetMapping("/{riderId}")
    @PreAuthorize("hasRole('PROMOTER')")
    public ResponseEntity<RiderResponse> getById(@PathVariable String riderId) {
        return ResponseEntity.ok(mapper.toResponse(getUseCase.execute(riderId)));
    }

    // ── PUT /api/v1/riders/{riderId} ─────────────────────────────────────────
    @PutMapping("/{riderId}")
    @PreAuthorize("hasRole('PROMOTER')")
    public ResponseEntity<RiderResponse> update(
            @PathVariable String riderId,
            @RequestBody UpdateRiderRequest request,
            @AuthenticationPrincipal String promoterId) {

        UpdateRiderCommand command = buildUpdateCommand(riderId, promoterId, request);
        return ResponseEntity.ok(mapper.toResponse(updateUseCase.execute(command)));
    }

    // ── POST /api/v1/riders/{riderId}/publish ────────────────────────────────
    @PostMapping("/{riderId}/publish")
    @PreAuthorize("hasRole('PROMOTER')")
    public ResponseEntity<RiderResponse> publish(
            @PathVariable String riderId,
            @AuthenticationPrincipal String promoterId) {

        return ResponseEntity.ok(mapper.toResponse(publishUseCase.execute(riderId, promoterId)));
    }

    // ── GET /api/v1/riders/{riderId}/pdf ─────────────────────────────────────
    @GetMapping("/{riderId}/pdf")
    @PreAuthorize("hasRole('PROMOTER')")
    public ResponseEntity<byte[]> downloadPdf(@PathVariable String riderId) {
        byte[] pdf = pdfUseCase.execute(riderId);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"rider-" + riderId + ".pdf\"")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }

    // ── POST /api/v1/riders/{riderId}/clone ──────────────────────────────────
    @PostMapping("/{riderId}/clone")
    @PreAuthorize("hasRole('PROMOTER')")
    public ResponseEntity<RiderResponse> clone(
            @PathVariable String riderId,
            @AuthenticationPrincipal String promoterId) {

        var cloned = cloneUseCase.execute(riderId, promoterId);
        return ResponseEntity
                .created(URI.create("/api/v1/riders/" + cloned.getId()))
                .body(mapper.toResponse(cloned));
    }

    // ── POST /api/v1/riders/{riderId}/share-link ─────────────────────────────
    @PostMapping("/{riderId}/share-link")
    @PreAuthorize("hasRole('PROMOTER')")
    public ResponseEntity<Map<String, String>> generateShareLink(
            @PathVariable String riderId,
            @AuthenticationPrincipal String promoterId) {

        String token = shareLinkUseCase.execute(riderId, promoterId);
        return ResponseEntity.ok(Map.of("shareToken", token));
    }

    // ── GET /api/v1/riders/{riderId}/public/{token} ──────────────────────────
    @GetMapping("/{riderId}/public/{token}")
    public ResponseEntity<RiderResponse> getPublic(
            @PathVariable String riderId,
            @PathVariable String token) {

        return ResponseEntity.ok(mapper.toResponse(publicRiderUseCase.execute(riderId, token)));
    }

    // ── POST /api/v1/riders/from-template ───────────────────────────────────
    @PostMapping("/from-template")
    @PreAuthorize("hasRole('PROMOTER')")
    public ResponseEntity<RiderResponse> createFromTemplate(
            @RequestBody @Valid FromTemplateRequest request,
            @AuthenticationPrincipal String promoterId) {

        var rider = fromTemplateUseCase.execute(
                request.template(), request.artistId(), request.name(), promoterId);
        return ResponseEntity
                .created(URI.create("/api/v1/riders/" + rider.getId()))
                .body(mapper.toResponse(rider));
    }

    // ── POST /api/v1/riders/{riderId}/line-items ─────────────────────────────
    @PostMapping("/{riderId}/line-items")
    @PreAuthorize("hasRole('PROMOTER')")
    public ResponseEntity<RiderLineItemResponse> addLineItem(
            @PathVariable String riderId,
            @RequestBody @Valid AddLineItemRequest request,
            @AuthenticationPrincipal String promoterId) {

        var item = addLineItemUseCase.execute(new AddLineItemCommand(riderId, promoterId, request.category(),
                request.description(), request.quantity(), request.required(), request.attributes(), request.notes()));
        return ResponseEntity.status(201).body(mapper.toLineItemResponse(item));
    }

    // ── DELETE /api/v1/riders/{riderId}/line-items/{lineItemId} ──────────────
    @DeleteMapping("/{riderId}/line-items/{lineItemId}")
    @PreAuthorize("hasRole('PROMOTER')")
    public ResponseEntity<Void> removeLineItem(
            @PathVariable String riderId,
            @PathVariable String lineItemId,
            @AuthenticationPrincipal String promoterId) {

        removeLineItemUseCase.execute(riderId, promoterId, lineItemId);
        return ResponseEntity.noContent().build();
    }

    // ── POST /api/v1/riders/{riderId}/line-items/{lineItemId}/fulfillment ────
    @PostMapping("/{riderId}/line-items/{lineItemId}/fulfillment")
    @PreAuthorize("hasRole('PROMOTER')")
    public ResponseEntity<RiderResponse> setFulfillment(
            @PathVariable String riderId,
            @PathVariable String lineItemId,
            @RequestBody @Valid SetFulfillmentRequest request,
            @AuthenticationPrincipal String promoterId) {

        var rider = setFulfillmentUseCase.execute(riderId, promoterId, lineItemId, request.fulfillmentSource());
        return ResponseEntity.ok(mapper.toResponse(rider));
    }

    // ── POST /api/v1/riders/{riderId}/line-items/{lineItemId}/substitution ───
    @PostMapping("/{riderId}/line-items/{lineItemId}/substitution")
    @PreAuthorize("hasRole('PROMOTER')")
    public ResponseEntity<RiderResponse> proposeSubstitution(
            @PathVariable String riderId,
            @PathVariable String lineItemId,
            @RequestBody @Valid ProposeSubstitutionRequest request,
            @AuthenticationPrincipal String promoterId) {

        var rider = proposeSubstitutionUseCase.execute(new ProposeEquipmentSubstitutionCommand(
                riderId, promoterId, lineItemId, request.proposedAlternative(), request.proposedBy(), request.notes()));
        return ResponseEntity.ok(mapper.toResponse(rider));
    }

    // ── POST /api/v1/riders/{riderId}/line-items/{lineItemId}/substitution/decision ──
    @PostMapping("/{riderId}/line-items/{lineItemId}/substitution/decision")
    @PreAuthorize("hasRole('PROMOTER')")
    public ResponseEntity<RiderResponse> decideSubstitution(
            @PathVariable String riderId,
            @PathVariable String lineItemId,
            @RequestBody @Valid DecideSubstitutionRequest request,
            @AuthenticationPrincipal String promoterId) {

        var rider = decideSubstitutionUseCase.execute(riderId, promoterId, lineItemId, request.approve());
        return ResponseEntity.ok(mapper.toResponse(rider));
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private UpdateRiderCommand buildUpdateCommand(String riderId, String promoterId, UpdateRiderRequest r) {
        List<UpdateRiderCommand.StaffData> staff = r.staff() == null ? null :
                r.staff().stream().map(s -> new UpdateRiderCommand.StaffData(s.role(), s.name())).toList();

        UpdateRiderCommand.StageDimensionsData sd = r.stageDimensions() == null ? null :
                new UpdateRiderCommand.StageDimensionsData(r.stageDimensions().widthMeters(),
                        r.stageDimensions().depthMeters(), r.stageDimensions().minHeightMeters(),
                        r.stageDimensions().powerOutlets(), r.stageDimensions().hasDrumRiser());

        List<UpdateRiderCommand.StageElementData> elements = r.stageElements() == null ? null :
                r.stageElements().stream().map(e -> new UpdateRiderCommand.StageElementData(
                        e.elementId(), e.type(), e.xPercent(), e.yPercent(),
                        e.rotationDegrees(), e.label())).toList();

        return new UpdateRiderCommand(riderId, promoterId, r.name(),
                r.soundCheckDurationMinutes(), r.soundCheckNotes(),
                staff, sd, elements, r.additionalNotes());
    }
}

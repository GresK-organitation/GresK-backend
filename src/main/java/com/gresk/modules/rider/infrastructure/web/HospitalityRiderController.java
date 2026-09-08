package com.gresk.modules.rider.infrastructure.web;

import com.gresk.modules.rider.application.command.AddLineItemCommand;
import com.gresk.modules.rider.application.command.CreateHospitalityRiderCommand;
import com.gresk.modules.rider.application.command.UpdateHospitalityRiderCommand;
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
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/v1/hospitality-riders")
@RequiredArgsConstructor
public class HospitalityRiderController {

    private final CreateHospitalityRiderUseCase        createUseCase;
    private final GetHospitalityRiderUseCase           getUseCase;
    private final UpdateHospitalityRiderUseCase        updateUseCase;
    private final PublishHospitalityRiderUseCase       publishUseCase;
    private final GenerateHospitalityRiderPdfUseCase   pdfUseCase;
    private final CloneHospitalityRiderUseCase         cloneUseCase;
    private final GenerateHospitalityShareLinkUseCase  shareLinkUseCase;
    private final GetPublicHospitalityRiderUseCase     publicRiderUseCase;
    private final AddHospitalityLineItemUseCase        addLineItemUseCase;
    private final RemoveHospitalityLineItemUseCase     removeLineItemUseCase;
    private final SetHospitalityLineItemFulfillmentUseCase setFulfillmentUseCase;
    private final RiderResponseMapper                  mapper;

    // ── POST /api/v1/hospitality-riders ──────────────────────────────────────
    @PostMapping
    @PreAuthorize("hasRole('PROMOTER')")
    public ResponseEntity<HospitalityRiderResponse> create(
            @RequestBody @Valid CreateHospitalityRiderRequest request,
            @AuthenticationPrincipal String promoterId) {

        var rider = createUseCase.execute(
                new CreateHospitalityRiderCommand(promoterId, request.artistId(), request.name()));
        return ResponseEntity
                .created(URI.create("/api/v1/hospitality-riders/" + rider.getId()))
                .body(mapper.toResponse(rider));
    }

    // ── GET /api/v1/hospitality-riders/{riderId} ─────────────────────────────
    @GetMapping("/{riderId}")
    @PreAuthorize("hasRole('PROMOTER')")
    public ResponseEntity<HospitalityRiderResponse> getById(@PathVariable String riderId) {
        return ResponseEntity.ok(mapper.toResponse(getUseCase.execute(riderId)));
    }

    // ── PUT /api/v1/hospitality-riders/{riderId} ─────────────────────────────
    @PutMapping("/{riderId}")
    @PreAuthorize("hasRole('PROMOTER')")
    public ResponseEntity<HospitalityRiderResponse> update(
            @PathVariable String riderId,
            @RequestBody UpdateHospitalityRiderRequest request,
            @AuthenticationPrincipal String promoterId) {

        var rider = updateUseCase.execute(new UpdateHospitalityRiderCommand(
                riderId, promoterId, request.name(), request.additionalNotes()));
        return ResponseEntity.ok(mapper.toResponse(rider));
    }

    // ── POST /api/v1/hospitality-riders/{riderId}/publish ────────────────────
    @PostMapping("/{riderId}/publish")
    @PreAuthorize("hasRole('PROMOTER')")
    public ResponseEntity<HospitalityRiderResponse> publish(
            @PathVariable String riderId,
            @AuthenticationPrincipal String promoterId) {

        return ResponseEntity.ok(mapper.toResponse(publishUseCase.execute(riderId, promoterId)));
    }

    // ── GET /api/v1/hospitality-riders/{riderId}/pdf ─────────────────────────
    @GetMapping("/{riderId}/pdf")
    @PreAuthorize("hasRole('PROMOTER')")
    public ResponseEntity<byte[]> downloadPdf(@PathVariable String riderId) {
        byte[] pdf = pdfUseCase.execute(riderId);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"hospitality-rider-" + riderId + ".pdf\"")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }

    // ── POST /api/v1/hospitality-riders/{riderId}/clone ──────────────────────
    @PostMapping("/{riderId}/clone")
    @PreAuthorize("hasRole('PROMOTER')")
    public ResponseEntity<HospitalityRiderResponse> clone(
            @PathVariable String riderId,
            @AuthenticationPrincipal String promoterId) {

        var cloned = cloneUseCase.execute(riderId, promoterId);
        return ResponseEntity
                .created(URI.create("/api/v1/hospitality-riders/" + cloned.getId()))
                .body(mapper.toResponse(cloned));
    }

    // ── POST /api/v1/hospitality-riders/{riderId}/share-link ─────────────────
    @PostMapping("/{riderId}/share-link")
    @PreAuthorize("hasRole('PROMOTER')")
    public ResponseEntity<Map<String, String>> generateShareLink(
            @PathVariable String riderId,
            @AuthenticationPrincipal String promoterId) {

        String token = shareLinkUseCase.execute(riderId, promoterId);
        return ResponseEntity.ok(Map.of("shareToken", token));
    }

    // ── GET /api/v1/hospitality-riders/{riderId}/public/{token} ──────────────
    @GetMapping("/{riderId}/public/{token}")
    public ResponseEntity<HospitalityRiderResponse> getPublic(
            @PathVariable String riderId,
            @PathVariable String token) {

        return ResponseEntity.ok(mapper.toResponse(publicRiderUseCase.execute(riderId, token)));
    }

    // ── POST /api/v1/hospitality-riders/{riderId}/line-items ─────────────────
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

    // ── DELETE /api/v1/hospitality-riders/{riderId}/line-items/{lineItemId} ──
    @DeleteMapping("/{riderId}/line-items/{lineItemId}")
    @PreAuthorize("hasRole('PROMOTER')")
    public ResponseEntity<Void> removeLineItem(
            @PathVariable String riderId,
            @PathVariable String lineItemId,
            @AuthenticationPrincipal String promoterId) {

        removeLineItemUseCase.execute(riderId, promoterId, lineItemId);
        return ResponseEntity.noContent().build();
    }

    // ── POST /api/v1/hospitality-riders/{riderId}/line-items/{lineItemId}/fulfillment ──
    @PostMapping("/{riderId}/line-items/{lineItemId}/fulfillment")
    @PreAuthorize("hasRole('PROMOTER')")
    public ResponseEntity<HospitalityRiderResponse> setFulfillment(
            @PathVariable String riderId,
            @PathVariable String lineItemId,
            @RequestBody @Valid SetFulfillmentRequest request,
            @AuthenticationPrincipal String promoterId) {

        var rider = setFulfillmentUseCase.execute(riderId, promoterId, lineItemId, request.fulfillmentSource());
        return ResponseEntity.ok(mapper.toResponse(rider));
    }
}

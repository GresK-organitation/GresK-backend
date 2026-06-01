package com.gresk.modules.rider.infrastructure.web;

import com.gresk.modules.rider.application.command.CreateRiderCommand;
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

    // ── Helpers ───────────────────────────────────────────────────────────────

    private UpdateRiderCommand buildUpdateCommand(String riderId, String promoterId, UpdateRiderRequest r) {
        List<UpdateRiderCommand.StaffData> staff = r.staff() == null ? null :
                r.staff().stream().map(s -> new UpdateRiderCommand.StaffData(s.role(), s.name())).toList();

        List<UpdateRiderCommand.InputChannelData> channels = r.inputChannels() == null ? null :
                r.inputChannels().stream().map(c -> new UpdateRiderCommand.InputChannelData(
                        c.channelNumber(), c.instrument(), c.microphone(), c.inserts(), c.notes())).toList();

        UpdateRiderCommand.SoundSystemData ss = r.soundSystem() == null ? null :
                new UpdateRiderCommand.SoundSystemData(r.soundSystem().consoleBrand(),
                        r.soundSystem().consoleChannels(), r.soundSystem().monitorMixes(),
                        r.soundSystem().paDescription(), r.soundSystem().processorNotes());

        List<UpdateRiderCommand.BacklineItemData> backline = r.backlineItems() == null ? null :
                r.backlineItems().stream().map(b -> new UpdateRiderCommand.BacklineItemData(
                        b.category(), b.description(), b.brand(), b.model(), b.required())).toList();

        UpdateRiderCommand.StageDimensionsData sd = r.stageDimensions() == null ? null :
                new UpdateRiderCommand.StageDimensionsData(r.stageDimensions().widthMeters(),
                        r.stageDimensions().depthMeters(), r.stageDimensions().minHeightMeters(),
                        r.stageDimensions().powerOutlets(), r.stageDimensions().hasDrumRiser());

        List<UpdateRiderCommand.StageElementData> elements = r.stageElements() == null ? null :
                r.stageElements().stream().map(e -> new UpdateRiderCommand.StageElementData(
                        e.elementId(), e.type(), e.xPercent(), e.yPercent(),
                        e.rotationDegrees(), e.label())).toList();

        UpdateRiderCommand.HospitalityData hosp = r.hospitality() == null ? null :
                new UpdateRiderCommand.HospitalityData(r.hospitality().dressingRoomCapacity(),
                        r.hospitality().cateringNotes(), r.hospitality().waterBottlesOnStage(),
                        r.hospitality().passesCount());

        UpdateRiderCommand.TransportData transport = r.transport() == null ? null :
                new UpdateRiderCommand.TransportData(r.transport().vehicleType(),
                        r.transport().passengerCapacity(), r.transport().notes());

        return new UpdateRiderCommand(riderId, promoterId, r.name(),
                r.soundCheckDurationMinutes(), r.soundCheckNotes(),
                staff, channels, ss, backline, sd, elements, hosp, transport, r.additionalNotes());
    }
}

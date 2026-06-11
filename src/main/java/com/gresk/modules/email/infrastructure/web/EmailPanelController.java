package com.gresk.modules.email.infrastructure.web;

import com.gresk.modules.email.application.dto.EventEmailSummary;
import com.gresk.modules.email.application.usecase.*;
import com.gresk.modules.email.infrastructure.web.dto.EmailDetailResponse;
import com.gresk.modules.email.infrastructure.web.dto.EmailResponse;
import com.gresk.modules.email.infrastructure.web.dto.LinkEmailRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/** Panel de comunicaciones del Email Intelligence Engine. */
@Tag(name = "Email Panel", description = "Timeline, resumen y gestión de emails del promotor")
@RestController
@RequestMapping("/api/v1/promoters/me")
@RequiredArgsConstructor
@PreAuthorize("hasRole('PROMOTER')")
public class EmailPanelController {

    private final GetEventEmailTimelineUseCase timeline;
    private final GetEventEmailSummaryUseCase  summary;
    private final GetPromoterEmailsUseCase     promoterEmails;
    private final GetEmailDetailUseCase        emailDetail;
    private final LinkEmailToEventUseCase      linkToEvent;
    private final EmailResponseMapper          mapper;

    @Operation(summary = "Timeline paginado de emails de un evento",
            description = "Correos vinculados al evento en orden cronológico inverso.")
    @ApiResponse(responseCode = "200", description = "Página del timeline")
    @ApiResponse(responseCode = "404", description = "El evento no existe o no pertenece al promotor")
    @GetMapping("/events/{eventId}/emails")
    public ResponseEntity<List<EmailResponse>> eventTimeline(
            @PathVariable UUID eventId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @AuthenticationPrincipal String promoterId) {
        return ResponseEntity.ok(timeline.execute(eventId, UUID.fromString(promoterId), page, size)
                .stream().map(mapper::toResponse).toList());
    }

    @Operation(summary = "Resumen de comunicaciones de un evento",
            description = "Conteos por clasificación, entidades clave detectadas por IA, "
                    + "acciones pendientes, versión activa del rider y borradores por revisar.")
    @ApiResponse(responseCode = "200", description = "Resumen del evento", content = @Content(
            examples = @ExampleObject(value = """
                    {"eventId": "7f8b...", "eventName": "Festival Indie GresK",
                     "emailCount": 12, "lastEmailAt": "2026-06-10T18:30:00Z",
                     "pendingActions": 2,
                     "classificationCounts": {"RIDER": 3, "HORARIO": 2},
                     "keyEntities": {"cache_acordado": "1800 EUR"},
                     "riderVersion": 2, "riderLastUpdated": "2026-06-09T10:00:00Z",
                     "riderPendingItems": ["6 monitores de escenario"],
                     "pendingDrafts": 1}
                    """)))
    @GetMapping("/events/{eventId}/emails/summary")
    public ResponseEntity<EventEmailSummary> eventSummary(@PathVariable UUID eventId,
                                                          @AuthenticationPrincipal String promoterId) {
        return ResponseEntity.ok(summary.execute(eventId, UUID.fromString(promoterId)));
    }

    @Operation(summary = "Bandeja completa del promotor")
    @GetMapping("/emails")
    public ResponseEntity<List<EmailResponse>> allEmails(@AuthenticationPrincipal String promoterId) {
        return ResponseEntity.ok(promoterEmails.execute(UUID.fromString(promoterId))
                .stream().map(mapper::toResponse).toList());
    }

    @Operation(summary = "Detalle de un email (cuerpo incluido)")
    @ApiResponse(responseCode = "404", description = "Email no encontrado")
    @ApiResponse(responseCode = "403", description = "El email pertenece a otro promotor")
    @GetMapping("/emails/{emailId}")
    public ResponseEntity<EmailDetailResponse> emailById(@PathVariable UUID emailId,
                                                         @AuthenticationPrincipal String promoterId) {
        return ResponseEntity.ok(mapper.toDetailResponse(
                emailDetail.execute(emailId, UUID.fromString(promoterId))));
    }

    @Operation(summary = "Vincular manualmente un email a un evento",
            description = "Para correos que el pipeline no pudo asociar automáticamente.")
    @ApiResponse(responseCode = "404", description = "El evento no existe o no pertenece al promotor")
    @PatchMapping("/emails/{emailId}/link")
    public ResponseEntity<EmailDetailResponse> linkEmail(@PathVariable UUID emailId,
                                                         @RequestBody @Valid LinkEmailRequest request,
                                                         @AuthenticationPrincipal String promoterId) {
        return ResponseEntity.ok(mapper.toDetailResponse(
                linkToEvent.execute(emailId, request.eventId(), UUID.fromString(promoterId))));
    }
}

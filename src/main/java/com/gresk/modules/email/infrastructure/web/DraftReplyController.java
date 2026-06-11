package com.gresk.modules.email.infrastructure.web;

import com.gresk.modules.email.application.command.ApproveDraftReplyCommand;
import com.gresk.modules.email.application.dto.DraftDetail;
import com.gresk.modules.email.application.usecase.*;
import com.gresk.modules.email.infrastructure.web.dto.*;
import io.swagger.v3.oas.annotations.Operation;
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

/** Borradores de respuesta generados por IA, pendientes de revisión humana. */
@Tag(name = "Draft Replies", description = "Revisión, edición, aprobación y envío de borradores")
@RestController
@RequestMapping("/api/v1/promoters/me/drafts")
@RequiredArgsConstructor
@PreAuthorize("hasRole('PROMOTER')")
public class DraftReplyController {

    private final GetPendingDraftsUseCase  pendingDrafts;
    private final GetDraftDetailUseCase    draftDetail;
    private final EditDraftUseCase         editDraft;
    private final ApproveDraftReplyUseCase approveDraft;
    private final DiscardDraftUseCase      discardDraft;
    private final EmailResponseMapper      mapper;

    @Operation(summary = "Borradores pendientes de revisión")
    @GetMapping
    public ResponseEntity<List<DraftReplyResponse>> pending(@AuthenticationPrincipal String promoterId) {
        return ResponseEntity.ok(pendingDrafts.execute(UUID.fromString(promoterId))
                .stream().map(mapper::toResponse).toList());
    }

    @Operation(summary = "Detalle de un borrador con el email original al que responde")
    @ApiResponse(responseCode = "404", description = "Borrador no encontrado")
    @ApiResponse(responseCode = "403", description = "El borrador pertenece a otro promotor")
    @GetMapping("/{draftId}")
    public ResponseEntity<DraftDetailResponse> detail(@PathVariable UUID draftId,
                                                      @AuthenticationPrincipal String promoterId) {
        DraftDetail detail = draftDetail.execute(draftId, UUID.fromString(promoterId));
        return ResponseEntity.ok(new DraftDetailResponse(
                mapper.toResponse(detail.draft()),
                mapper.toDetailResponse(detail.originalEmail())));
    }

    @Operation(summary = "Editar el cuerpo de un borrador antes de aprobarlo")
    @ApiResponse(responseCode = "409", description = "El borrador ya no está en PENDING_REVIEW")
    @PutMapping("/{draftId}")
    public ResponseEntity<DraftReplyResponse> edit(@PathVariable UUID draftId,
                                                   @RequestBody @Valid EditDraftRequest request,
                                                   @AuthenticationPrincipal String promoterId) {
        return ResponseEntity.ok(mapper.toResponse(
                editDraft.execute(draftId, UUID.fromString(promoterId), request.body())));
    }

    @Operation(summary = "Aprobar un borrador y enviarlo al remitente original",
            description = "Si el envío SMTP falla, la aprobación revierte y el "
                    + "borrador permanece en PENDING_REVIEW.")
    @ApiResponse(responseCode = "200", description = "Borrador aprobado y enviado")
    @ApiResponse(responseCode = "409", description = "El borrador ya fue enviado o descartado")
    @PostMapping("/{draftId}/approve")
    public ResponseEntity<DraftReplyResponse> approve(@PathVariable UUID draftId,
                                                      @RequestBody(required = false) ApproveDraftRequest request,
                                                      @AuthenticationPrincipal String promoterId) {
        return ResponseEntity.ok(mapper.toResponse(approveDraft.execute(new ApproveDraftReplyCommand(
                draftId, UUID.fromString(promoterId),
                request != null ? request.editedBody() : null))));
    }

    @Operation(summary = "Descartar un borrador pendiente")
    @ApiResponse(responseCode = "409", description = "El borrador ya no está en PENDING_REVIEW")
    @DeleteMapping("/{draftId}")
    public ResponseEntity<Void> discard(@PathVariable UUID draftId,
                                        @AuthenticationPrincipal String promoterId) {
        discardDraft.execute(draftId, UUID.fromString(promoterId));
        return ResponseEntity.noContent().build();
    }
}

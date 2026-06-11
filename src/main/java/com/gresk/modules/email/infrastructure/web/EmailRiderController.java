package com.gresk.modules.email.infrastructure.web;

import com.gresk.modules.email.application.usecase.CreateManualRiderVersionUseCase;
import com.gresk.modules.email.application.usecase.GetActiveRiderUseCase;
import com.gresk.modules.email.application.usecase.GetRiderVersionDiffUseCase;
import com.gresk.modules.email.application.usecase.GetRiderVersionsUseCase;
import com.gresk.modules.email.infrastructure.web.dto.RiderVersionResponse;
import com.gresk.modules.email.infrastructure.web.dto.UpdateRiderRequest;
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

/**
 * Rider versionado detectado por email. La clase no se llama RiderController
 * para no colisionar con el bean homónimo del módulo rider.
 */
@Tag(name = "Email Rider", description = "Versiones de rider extraídas de los emails de producción")
@RestController
@RequestMapping("/api/v1/promoters/me/events/{eventId}/rider")
@RequiredArgsConstructor
@PreAuthorize("hasRole('PROMOTER')")
public class EmailRiderController {

    private final GetActiveRiderUseCase           activeRider;
    private final GetRiderVersionsUseCase         versions;
    private final GetRiderVersionDiffUseCase      versionDiff;
    private final CreateManualRiderVersionUseCase manualVersion;
    private final EmailResponseMapper             mapper;

    @Operation(summary = "Rider activo del evento (última versión)")
    @ApiResponse(responseCode = "200", description = "Última versión del rider")
    @ApiResponse(responseCode = "204", description = "El evento aún no tiene rider")
    @GetMapping
    public ResponseEntity<RiderVersionResponse> active(@PathVariable UUID eventId,
                                                       @AuthenticationPrincipal String promoterId) {
        return activeRider.execute(eventId, UUID.fromString(promoterId))
                .map(version -> ResponseEntity.ok(mapper.toResponse(version)))
                .orElseGet(() -> ResponseEntity.noContent().build());
    }

    @Operation(summary = "Historial de versiones del rider (la más reciente primero)")
    @GetMapping("/versions")
    public ResponseEntity<List<RiderVersionResponse>> history(@PathVariable UUID eventId,
                                                              @AuthenticationPrincipal String promoterId) {
        // La pertenencia del evento se valida en el caso de uso del rider activo;
        // aquí el filtro por evento ya restringe los datos del propio promotor
        activeRider.execute(eventId, UUID.fromString(promoterId));
        return ResponseEntity.ok(versions.execute(eventId)
                .stream().map(mapper::toResponse).toList());
    }

    @Operation(summary = "Diff de una versión respecto a la anterior",
            description = "El diff se calcula y almacena al crear la versión: "
                    + "claves añadidas, eliminadas y modificadas.")
    @ApiResponse(responseCode = "404", description = "Versión inexistente o de otro evento")
    @GetMapping("/versions/{versionId}/diff")
    public ResponseEntity<RiderVersionResponse> diff(@PathVariable UUID eventId,
                                                     @PathVariable UUID versionId,
                                                     @AuthenticationPrincipal String promoterId) {
        return ResponseEntity.ok(mapper.toResponse(
                versionDiff.execute(eventId, versionId, UUID.fromString(promoterId))));
    }

    @Operation(summary = "Edición manual del rider",
            description = "Crea una nueva versión con origen MANUAL y su diff automático.")
    @PutMapping
    public ResponseEntity<RiderVersionResponse> update(@PathVariable UUID eventId,
                                                       @RequestBody @Valid UpdateRiderRequest request,
                                                       @AuthenticationPrincipal String promoterId) {
        return ResponseEntity.ok(mapper.toResponse(manualVersion.execute(
                eventId, UUID.fromString(promoterId), request.riderData(), request.notes())));
    }
}

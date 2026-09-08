package com.gresk.modules.contract.infrastructure.web;

import com.gresk.modules.contract.application.command.CreateClauseTemplateCommand;
import com.gresk.modules.contract.application.usecase.*;
import com.gresk.modules.contract.domain.model.ContractType;
import com.gresk.modules.contract.infrastructure.web.dto.ClauseTemplateResponse;
import com.gresk.modules.contract.infrastructure.web.dto.CreateClauseTemplateRequest;
import com.gresk.modules.contract.infrastructure.web.dto.UpdateClauseTemplateRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/v1/clause-templates")
@RequiredArgsConstructor
public class ClauseTemplateController {

    private final ListClauseTemplatesUseCase      listUseCase;
    private final CreateCustomClauseTemplateUseCase createUseCase;
    private final UpdateClauseTemplateUseCase      updateUseCase;
    private final DeactivateClauseTemplateUseCase  deactivateUseCase;
    private final ClauseTemplateResponseMapper     mapper;

    @GetMapping
    @PreAuthorize("hasRole('PROMOTER')")
    public ResponseEntity<List<ClauseTemplateResponse>> list(
            @RequestParam(required = false) ContractType type,
            @AuthenticationPrincipal String promoterId) {
        var result = listUseCase.execute(promoterId, type).stream().map(mapper::toResponse).toList();
        return ResponseEntity.ok(result);
    }

    @PostMapping
    @PreAuthorize("hasRole('PROMOTER')")
    public ResponseEntity<ClauseTemplateResponse> create(
            @RequestBody @Valid CreateClauseTemplateRequest request,
            @AuthenticationPrincipal String promoterId) {

        var template = createUseCase.execute(new CreateClauseTemplateCommand(
                promoterId, request.code(), request.category(), request.title(),
                request.contentTemplate(), request.applicableTypes(), request.jurisdictionScope()));

        return ResponseEntity.created(URI.create("/api/v1/clause-templates/" + template.getId()))
                .body(mapper.toResponse(template));
    }

    @PutMapping("/{templateId}")
    @PreAuthorize("hasRole('PROMOTER')")
    public ResponseEntity<ClauseTemplateResponse> update(
            @PathVariable String templateId,
            @RequestBody @Valid UpdateClauseTemplateRequest request,
            @AuthenticationPrincipal String promoterId) {

        var template = updateUseCase.execute(templateId, promoterId, request.title(), request.contentTemplate());
        return ResponseEntity.ok(mapper.toResponse(template));
    }

    @DeleteMapping("/{templateId}")
    @PreAuthorize("hasRole('PROMOTER')")
    public ResponseEntity<Void> deactivate(
            @PathVariable String templateId,
            @AuthenticationPrincipal String promoterId) {
        deactivateUseCase.execute(templateId, promoterId);
        return ResponseEntity.noContent().build();
    }
}

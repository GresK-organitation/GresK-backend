package com.gresk.modules.contract.infrastructure.web;

import com.gresk.modules.contract.application.command.CreateContractTemplateCommand;
import com.gresk.modules.contract.application.usecase.*;
import com.gresk.modules.contract.domain.model.ContractType;
import com.gresk.modules.contract.infrastructure.web.dto.ContractTemplateResponse;
import com.gresk.modules.contract.infrastructure.web.dto.CreateContractTemplateRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/contract-templates")
@RequiredArgsConstructor
public class ContractTemplateController {

    private final ListContractTemplatesUseCase   listUseCase;
    private final CreateContractTemplateUseCase  createUseCase;
    private final UpdateContractTemplateUseCase  updateUseCase;
    private final GetContractTemplateUseCase     getUseCase;
    private final ContractTemplateResponseMapper mapper;

    @GetMapping
    @PreAuthorize("hasRole('PROMOTER')")
    public ResponseEntity<List<ContractTemplateResponse>> list(
            @RequestParam(required = false) ContractType type,
            @AuthenticationPrincipal String promoterId) {
        var result = listUseCase.execute(promoterId, type).stream().map(mapper::toResponse).toList();
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{templateId}")
    @PreAuthorize("hasRole('PROMOTER')")
    public ResponseEntity<ContractTemplateResponse> get(@PathVariable String templateId) {
        return ResponseEntity.ok(mapper.toResponse(getUseCase.execute(templateId)));
    }

    @PostMapping
    @PreAuthorize("hasRole('PROMOTER')")
    public ResponseEntity<ContractTemplateResponse> create(
            @RequestBody @Valid CreateContractTemplateRequest request,
            @AuthenticationPrincipal String promoterId) {

        var template = createUseCase.execute(toCommand(request, promoterId));
        return ResponseEntity.created(URI.create("/api/v1/contract-templates/" + template.getId()))
                .body(mapper.toResponse(template));
    }

    @PutMapping("/{templateId}")
    @PreAuthorize("hasRole('PROMOTER')")
    public ResponseEntity<ContractTemplateResponse> update(
            @PathVariable String templateId,
            @RequestBody @Valid CreateContractTemplateRequest request,
            @AuthenticationPrincipal String promoterId) {

        var template = updateUseCase.execute(templateId, toCommand(request, promoterId));
        return ResponseEntity.ok(mapper.toResponse(template));
    }

    private CreateContractTemplateCommand toCommand(CreateContractTemplateRequest r, String promoterId) {
        return new CreateContractTemplateCommand(
                promoterId, r.type(), r.name(), r.bodyMarkdown(),
                r.variables() != null ? r.variables() : List.of(),
                r.defaultClauseTemplateIds() != null ? r.defaultClauseTemplateIds() : List.of());
    }
}

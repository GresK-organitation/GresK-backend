package com.gresk.modules.contract.infrastructure.web;

import com.gresk.modules.contract.application.usecase.CreateContractVersionUseCase;
import com.gresk.modules.contract.application.usecase.GetContractAuditTrailUseCase;
import com.gresk.modules.contract.application.usecase.GetContractVersionUseCase;
import com.gresk.modules.contract.application.usecase.GetContractVersionsUseCase;
import com.gresk.modules.contract.infrastructure.web.dto.AuditTrailEntryResponse;
import com.gresk.modules.contract.infrastructure.web.dto.ContractVersionResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/contracts/{contractId}")
@RequiredArgsConstructor
public class ContractVersionController {

    private final CreateContractVersionUseCase createVersionUseCase;
    private final GetContractVersionsUseCase   listVersionsUseCase;
    private final GetContractVersionUseCase    getVersionUseCase;
    private final GetContractAuditTrailUseCase getAuditTrailUseCase;
    private final ContractResponseMapper       mapper;

    @PostMapping("/versions")
    @PreAuthorize("hasRole('PROMOTER')")
    public ResponseEntity<ContractVersionResponse> createVersion(
            @PathVariable String contractId,
            @RequestBody(required = false) Map<String, String> body,
            @AuthenticationPrincipal String promoterId) {

        String changeSummary = body != null ? body.getOrDefault("changeSummary", null) : null;
        var version = createVersionUseCase.execute(contractId, promoterId, changeSummary);
        return ResponseEntity.status(201).body(mapper.toVersionResponse(version));
    }

    @GetMapping("/versions")
    @PreAuthorize("hasRole('PROMOTER')")
    public ResponseEntity<List<ContractVersionResponse>> listVersions(
            @PathVariable String contractId,
            @AuthenticationPrincipal String promoterId) {

        var versions = listVersionsUseCase.execute(contractId, promoterId).stream()
                .map(mapper::toVersionResponse).toList();
        return ResponseEntity.ok(versions);
    }

    @GetMapping("/versions/{versionId}")
    @PreAuthorize("hasRole('PROMOTER')")
    public ResponseEntity<ContractVersionResponse> getVersion(
            @PathVariable String contractId,
            @PathVariable String versionId,
            @AuthenticationPrincipal String promoterId) {

        var version = getVersionUseCase.execute(contractId, versionId, promoterId);
        return ResponseEntity.ok(mapper.toVersionResponse(version));
    }

    @GetMapping("/audit-trail")
    @PreAuthorize("hasRole('PROMOTER')")
    public ResponseEntity<List<AuditTrailEntryResponse>> auditTrail(
            @PathVariable String contractId,
            @AuthenticationPrincipal String promoterId) {

        var entries = getAuditTrailUseCase.execute(contractId, promoterId).stream()
                .map(mapper::toAuditTrailEntryResponse).toList();
        return ResponseEntity.ok(entries);
    }
}

package com.gresk.modules.contract.infrastructure.web;

import com.gresk.modules.contract.application.command.CreateContractCommand;
import com.gresk.modules.contract.application.command.CreateContractFromTemplateCommand;
import com.gresk.modules.contract.application.command.UpdateContractCommand;
import com.gresk.modules.contract.application.usecase.*;
import com.gresk.modules.contract.domain.model.ContractStatus;
import com.gresk.modules.contract.domain.model.ContractType;
import com.gresk.modules.contract.infrastructure.web.dto.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.net.URI;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/v1/contracts")
@RequiredArgsConstructor
public class ContractController {

    private final CreateContractUseCase              createUseCase;
    private final CreateContractFromTemplateUseCase  fromTemplateUseCase;
    private final GetContractUseCase                 getUseCase;
    private final GetPromoterContractsUseCase        listUseCase;
    private final UpdateContractUseCase              updateUseCase;
    private final SendContractUseCase                sendUseCase;
    private final SignContractUseCase                signUseCase;
    private final ArchiveContractUseCase             archiveUseCase;
    private final CancelContractUseCase              cancelUseCase;
    private final CloneContractUseCase               cloneUseCase;
    private final GenerateContractPdfUseCase         pdfUseCase;
    private final RenderContractFromTemplateUseCase  renderFromTemplateUseCase;
    private final UploadSignedPdfUseCase             uploadPdfUseCase;
    private final GenerateShareLinkUseCase           shareLinkUseCase;
    private final GetContractStatsUseCase            statsUseCase;
    private final ContractResponseMapper             mapper;

    // ── POST /api/v1/contracts ────────────────────────────────────────────────
    @PostMapping
    @PreAuthorize("hasRole('PROMOTER')")
    public ResponseEntity<ContractResponse> create(
            @RequestBody @Valid CreateContractRequest request,
            @AuthenticationPrincipal String promoterId) {

        var contract = createUseCase.execute(new CreateContractCommand(
                promoterId, request.type(),
                request.partyAName(), request.partyATaxId(), request.partyAAddress(),
                request.partyASignatoryName(), request.partyASignatoryRole(), request.partyAEmail(),
                request.partyACountry(), request.partyATaxResident()));

        return ResponseEntity
                .created(URI.create("/api/v1/contracts/" + contract.getId()))
                .body(mapper.toResponse(contract));
    }

    // ── POST /api/v1/contracts/from-template ─────────────────────────────────
    @PostMapping("/from-template")
    @PreAuthorize("hasRole('PROMOTER')")
    public ResponseEntity<ContractResponse> createFromTemplate(
            @RequestBody @Valid CreateContractFromTemplateRequest request,
            @AuthenticationPrincipal String promoterId) {

        var contract = fromTemplateUseCase.execute(new CreateContractFromTemplateCommand(
                promoterId, request.type(),
                request.partyAName(), request.partyATaxId(), request.partyAAddress(),
                request.partyASignatoryName(), request.partyASignatoryRole(), request.partyAEmail(),
                request.linkedEventId(), request.linkedArtistId()));

        return ResponseEntity
                .created(URI.create("/api/v1/contracts/" + contract.getId()))
                .body(mapper.toResponse(contract));
    }

    // ── GET /api/v1/contracts ─────────────────────────────────────────────────
    @GetMapping
    @PreAuthorize("hasRole('PROMOTER')")
    public ResponseEntity<List<ContractSummaryResponse>> list(
            @RequestParam(required = false) ContractStatus status,
            @RequestParam(required = false) ContractType   type,
            @AuthenticationPrincipal String promoterId) {

        List<ContractSummaryResponse> result = listUseCase.execute(promoterId, status, type)
                .stream().map(mapper::toSummary).toList();
        return ResponseEntity.ok(result);
    }

    // ── GET /api/v1/contracts/stats ───────────────────────────────────────────
    @GetMapping("/stats")
    @PreAuthorize("hasRole('PROMOTER')")
    public ResponseEntity<ContractStatsResponse> stats(@AuthenticationPrincipal String promoterId) {
        return ResponseEntity.ok(mapper.toStatsResponse(statsUseCase.execute(promoterId)));
    }

    // ── GET /api/v1/contracts/{id} ────────────────────────────────────────────
    @GetMapping("/{contractId}")
    @PreAuthorize("hasRole('PROMOTER')")
    public ResponseEntity<ContractResponse> getById(
            @PathVariable String contractId,
            @AuthenticationPrincipal String promoterId) {
        return ResponseEntity.ok(mapper.toResponse(getUseCase.execute(contractId, promoterId)));
    }

    // ── PUT /api/v1/contracts/{id} ────────────────────────────────────────────
    @PutMapping("/{contractId}")
    @PreAuthorize("hasRole('PROMOTER')")
    public ResponseEntity<ContractResponse> update(
            @PathVariable String contractId,
            @RequestBody UpdateContractRequest request,
            @AuthenticationPrincipal String promoterId) {

        UpdateContractCommand cmd = buildUpdateCommand(contractId, promoterId, request);
        return ResponseEntity.ok(mapper.toResponse(updateUseCase.execute(cmd)));
    }

    // ── POST /api/v1/contracts/{id}/send ──────────────────────────────────────
    @PostMapping("/{contractId}/send")
    @PreAuthorize("hasRole('PROMOTER')")
    public ResponseEntity<ContractResponse> send(
            @PathVariable String contractId,
            @AuthenticationPrincipal String promoterId) {
        return ResponseEntity.ok(mapper.toResponse(sendUseCase.execute(contractId, promoterId)));
    }

    // ── POST /api/v1/contracts/{id}/sign ──────────────────────────────────────
    @PostMapping("/{contractId}/sign")
    @PreAuthorize("hasRole('PROMOTER')")
    public ResponseEntity<ContractResponse> sign(
            @PathVariable String contractId,
            @AuthenticationPrincipal String promoterId) {
        return ResponseEntity.ok(mapper.toResponse(signUseCase.execute(contractId, promoterId)));
    }

    // ── POST /api/v1/contracts/{id}/archive ───────────────────────────────────
    @PostMapping("/{contractId}/archive")
    @PreAuthorize("hasRole('PROMOTER')")
    public ResponseEntity<ContractResponse> archive(
            @PathVariable String contractId,
            @AuthenticationPrincipal String promoterId) {
        return ResponseEntity.ok(mapper.toResponse(archiveUseCase.execute(contractId, promoterId)));
    }

    // ── POST /api/v1/contracts/{id}/cancel ────────────────────────────────────
    @PostMapping("/{contractId}/cancel")
    @PreAuthorize("hasRole('PROMOTER')")
    public ResponseEntity<ContractResponse> cancel(
            @PathVariable String contractId,
            @AuthenticationPrincipal String promoterId) {
        return ResponseEntity.ok(mapper.toResponse(cancelUseCase.execute(contractId, promoterId)));
    }

    // ── POST /api/v1/contracts/{id}/clone ─────────────────────────────────────
    @PostMapping("/{contractId}/clone")
    @PreAuthorize("hasRole('PROMOTER')")
    public ResponseEntity<ContractResponse> clone(
            @PathVariable String contractId,
            @AuthenticationPrincipal String promoterId) {

        var cloned = cloneUseCase.execute(contractId, promoterId);
        return ResponseEntity
                .created(URI.create("/api/v1/contracts/" + cloned.getId()))
                .body(mapper.toResponse(cloned));
    }

    // ── GET /api/v1/contracts/{id}/pdf ────────────────────────────────────────
    @GetMapping("/{contractId}/pdf")
    @PreAuthorize("hasRole('PROMOTER')")
    public ResponseEntity<byte[]> downloadPdf(
            @PathVariable String contractId,
            @AuthenticationPrincipal String promoterId) {

        byte[] pdf = pdfUseCase.execute(contractId, promoterId);
        String filename = "contract-" + contractId + ".pdf";
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }

    // ── GET /api/v1/contracts/{id}/pdf/template/{templateId} ─────────────────
    @GetMapping("/{contractId}/pdf/template/{templateId}")
    @PreAuthorize("hasRole('PROMOTER')")
    public ResponseEntity<byte[]> downloadTemplatePdf(
            @PathVariable String contractId,
            @PathVariable String templateId,
            @AuthenticationPrincipal String promoterId) {

        byte[] pdf = renderFromTemplateUseCase.execute(contractId, templateId, promoterId);
        String filename = "contract-" + contractId + "-template.pdf";
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }

    // ── POST /api/v1/contracts/{id}/signed-pdf ────────────────────────────────
    @PostMapping(value = "/{contractId}/signed-pdf", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('PROMOTER')")
    public ResponseEntity<ContractResponse> uploadSignedPdf(
            @PathVariable String contractId,
            @RequestParam("file") MultipartFile file,
            @AuthenticationPrincipal String promoterId) {

        return ResponseEntity.ok(mapper.toResponse(
                uploadPdfUseCase.execute(contractId, promoterId, file)));
    }

    // ── POST /api/v1/contracts/{id}/share-link ────────────────────────────────
    @PostMapping("/{contractId}/share-link")
    @PreAuthorize("hasRole('PROMOTER')")
    public ResponseEntity<Map<String, String>> generateShareLink(
            @PathVariable String contractId,
            @AuthenticationPrincipal String promoterId) {

        String token = shareLinkUseCase.execute(contractId, promoterId);
        return ResponseEntity.ok(Map.of("shareToken", token));
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private UpdateContractCommand buildUpdateCommand(String contractId, String promoterId,
                                                      UpdateContractRequest r) {
        List<UpdateContractCommand.PaymentTermData> terms = r.paymentTerms() == null ? null :
                r.paymentTerms().stream()
                        .map(p -> new UpdateContractCommand.PaymentTermData(
                                p.percentage(), p.description(), p.method(), p.paid()))
                        .toList();

        List<UpdateContractCommand.ClauseData> clauses = r.clauses() == null ? null :
                r.clauses().stream()
                        .map(c -> new UpdateContractCommand.ClauseData(c.order(), c.title(), c.content()))
                        .toList();

        UpdateContractCommand.WithholdingTaxData wht = r.withholdingTax() == null ? null :
                new UpdateContractCommand.WithholdingTaxData(
                        r.withholdingTax().type(), r.withholdingTax().ratePercentage(),
                        r.withholdingTax().taxBase(), r.withholdingTax().withheldAmount(),
                        r.withholdingTax().exemptionReason());

        return new UpdateContractCommand(
                contractId, promoterId,
                r.partyB() != null ? r.partyB().name()          : null,
                r.partyB() != null ? r.partyB().taxId()         : null,
                r.partyB() != null ? r.partyB().address()       : null,
                r.partyB() != null ? r.partyB().signatoryName() : null,
                r.partyB() != null ? r.partyB().signatoryRole() : null,
                r.partyB() != null ? r.partyB().email()         : null,
                r.partyB() != null ? r.partyB().country()       : null,
                r.partyB() != null ? r.partyB().taxResident()   : null,
                r.perfVenue(), r.perfEventDate(), r.perfDurationMinutes(), r.perfShowTime(),
                r.feeAmount(), r.feeCurrency(), terms, wht,
                clauses,
                r.jurisdiction(), r.contractCity(), r.contractDate(),
                r.linkedEventId(), r.linkedArtistId(), r.linkedRiderId()
        );
    }
}

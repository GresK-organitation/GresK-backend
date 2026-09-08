package com.gresk.modules.artist.infrastructure.web;

import com.gresk.modules.artist.application.command.AddEpkAssetVersionCommand;
import com.gresk.modules.artist.application.command.GenerateEpkShareLinkCommand;
import com.gresk.modules.artist.application.command.UploadEpkAssetCommand;
import com.gresk.modules.artist.application.port.in.*;
import com.gresk.modules.artist.domain.model.EpkAsset;
import com.gresk.modules.artist.domain.model.EpkShareLink;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/v1/artists/{artistId}/epk")
@RequiredArgsConstructor
public class EpkAssetController {

    private final UploadEpkAssetPort       uploadUseCase;
    private final AddEpkAssetVersionPort   addVersionUseCase;
    private final ListEpkAssetsPort        listUseCase;
    private final ArchiveEpkAssetPort      archiveUseCase;
    private final GenerateEpkShareLinkPort shareLinkUseCase;
    private final RevokeEpkShareLinkPort   revokeShareLinkUseCase;
    private final EpkResponseMapper        mapper;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('PROMOTER')")
    public ResponseEntity<EpkAssetResponse> upload(
            @PathVariable String artistId,
            @RequestPart("data") @Valid UploadEpkAssetRequest request,
            @RequestPart("file") MultipartFile file,
            @AuthenticationPrincipal String promoterId) {

        EpkAsset asset = uploadUseCase.execute(new UploadEpkAssetCommand(
                artistId, promoterId, request.type(), request.label(), file, promoterId));

        return ResponseEntity
                .created(URI.create("/api/v1/artists/" + artistId + "/epk/" + asset.getId().value()))
                .body(mapper.toResponse(asset));
    }

    @PostMapping(value = "/{epkAssetId}/versions", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('PROMOTER')")
    public ResponseEntity<EpkAssetResponse> addVersion(
            @PathVariable String artistId,
            @PathVariable String epkAssetId,
            @RequestPart("file") MultipartFile file,
            @AuthenticationPrincipal String promoterId) {

        EpkAsset asset = addVersionUseCase.execute(
                new AddEpkAssetVersionCommand(epkAssetId, promoterId, file, promoterId));
        return ResponseEntity.ok(mapper.toResponse(asset));
    }

    @GetMapping
    @PreAuthorize("hasRole('PROMOTER')")
    public ResponseEntity<List<EpkAssetResponse>> list(
            @PathVariable String artistId,
            @AuthenticationPrincipal String promoterId) {
        return ResponseEntity.ok(listUseCase.execute(artistId, promoterId).stream()
                .map(mapper::toResponse).toList());
    }

    @PostMapping("/{epkAssetId}/archive")
    @PreAuthorize("hasRole('PROMOTER')")
    public ResponseEntity<Void> archive(
            @PathVariable String artistId,
            @PathVariable String epkAssetId,
            @AuthenticationPrincipal String promoterId) {
        archiveUseCase.execute(epkAssetId, promoterId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{epkAssetId}/share-link")
    @PreAuthorize("hasRole('PROMOTER')")
    public ResponseEntity<EpkShareLinkResponse> generateShareLink(
            @PathVariable String artistId,
            @PathVariable String epkAssetId,
            @RequestBody @Valid GenerateEpkShareLinkRequest request,
            @AuthenticationPrincipal String promoterId) {

        EpkShareLink link = shareLinkUseCase.execute(new GenerateEpkShareLinkCommand(
                epkAssetId, promoterId, request.versionNumber(),
                request.expiresInHours(), request.maxDownloads(), promoterId));
        return ResponseEntity.ok(mapper.toShareLinkResponse(link));
    }

    @PostMapping("/share-link/{shareLinkId}/revoke")
    @PreAuthorize("hasRole('PROMOTER')")
    public ResponseEntity<Void> revokeShareLink(
            @PathVariable String artistId,
            @PathVariable String shareLinkId,
            @AuthenticationPrincipal String promoterId) {
        revokeShareLinkUseCase.execute(shareLinkId, promoterId);
        return ResponseEntity.noContent().build();
    }
}

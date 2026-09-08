package com.gresk.modules.artist.infrastructure.web;

import com.gresk.modules.artist.application.command.AddRosterMemberCommand;
import com.gresk.modules.artist.application.command.UpdateRosterMemberCommand;
import com.gresk.modules.artist.application.port.in.*;
import com.gresk.modules.artist.domain.model.RosterMember;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/v1/artists/{artistId}/roster")
@RequiredArgsConstructor
public class RosterMemberController {

    private final AddRosterMemberPort         addUseCase;
    private final UpdateRosterMemberPort      updateUseCase;
    private final ListRosterMembersPort       listUseCase;
    private final DeactivateRosterMemberPort  deactivateUseCase;
    private final RosterMemberResponseMapper  mapper;

    @PostMapping
    @PreAuthorize("hasRole('PROMOTER')")
    public ResponseEntity<RosterMemberResponse> add(
            @PathVariable String artistId,
            @RequestBody @Valid AddRosterMemberRequest request,
            @AuthenticationPrincipal String promoterId) {

        RosterMember member = addUseCase.execute(new AddRosterMemberCommand(
                artistId, promoterId, request.name(), request.role(), request.phone(), request.email(),
                request.billingLegalName(), request.billingTaxId(), request.billingAddress(),
                request.billingIban(), request.primary()));

        return ResponseEntity
                .created(URI.create("/api/v1/artists/" + artistId + "/roster/" + member.getId().value()))
                .body(mapper.toResponse(member));
    }

    @GetMapping
    @PreAuthorize("hasRole('PROMOTER')")
    public ResponseEntity<List<RosterMemberResponse>> list(
            @PathVariable String artistId,
            @AuthenticationPrincipal String promoterId) {
        return ResponseEntity.ok(listUseCase.execute(artistId, promoterId).stream()
                .map(mapper::toResponse).toList());
    }

    @PutMapping("/{rosterMemberId}")
    @PreAuthorize("hasRole('PROMOTER')")
    public ResponseEntity<RosterMemberResponse> update(
            @PathVariable String artistId,
            @PathVariable String rosterMemberId,
            @RequestBody @Valid UpdateRosterMemberRequest request,
            @AuthenticationPrincipal String promoterId) {

        RosterMember member = updateUseCase.execute(new UpdateRosterMemberCommand(
                rosterMemberId, promoterId, request.name(), request.role(), request.phone(), request.email(),
                request.billingLegalName(), request.billingTaxId(), request.billingAddress(),
                request.billingIban(), request.primary(), request.active()));

        return ResponseEntity.ok(mapper.toResponse(member));
    }

    @PostMapping("/{rosterMemberId}/deactivate")
    @PreAuthorize("hasRole('PROMOTER')")
    public ResponseEntity<RosterMemberResponse> deactivate(
            @PathVariable String artistId,
            @PathVariable String rosterMemberId,
            @AuthenticationPrincipal String promoterId) {
        return ResponseEntity.ok(mapper.toResponse(deactivateUseCase.execute(rosterMemberId, promoterId)));
    }
}

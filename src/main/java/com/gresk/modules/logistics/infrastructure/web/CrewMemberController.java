package com.gresk.modules.logistics.infrastructure.web;

import com.gresk.modules.logistics.application.command.CreateCrewMemberCommand;
import com.gresk.modules.logistics.application.command.DeactivateCrewMemberCommand;
import com.gresk.modules.logistics.application.command.UpdateCrewMemberCommand;
import com.gresk.modules.logistics.application.dto.CrewMemberResponse;
import com.gresk.modules.logistics.application.dto.CrewMemberResponseMapper;
import com.gresk.modules.logistics.application.port.in.CreateCrewMemberUseCase;
import com.gresk.modules.logistics.application.port.in.DeactivateCrewMemberUseCase;
import com.gresk.modules.logistics.application.port.in.GetCrewMemberUseCase;
import com.gresk.modules.logistics.application.port.in.ListCrewMembersUseCase;
import com.gresk.modules.logistics.application.port.in.UpdateCrewMemberUseCase;
import com.gresk.modules.logistics.application.query.GetCrewMemberQuery;
import com.gresk.modules.logistics.application.query.ListCrewMembersQuery;
import com.gresk.modules.logistics.infrastructure.web.dto.CreateCrewMemberRequest;
import com.gresk.modules.logistics.infrastructure.web.dto.UpdateCrewMemberRequest;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/logistics/crew-members")
@RequiredArgsConstructor
@PreAuthorize("hasRole('PROMOTER')")
@Tag(name = "Logistics - Crew", description = "Personal de gira contratado por la promotora, sin vínculo a un Artist")
public class CrewMemberController {

    private final CreateCrewMemberUseCase createCrewMemberUseCase;
    private final UpdateCrewMemberUseCase updateCrewMemberUseCase;
    private final DeactivateCrewMemberUseCase deactivateCrewMemberUseCase;
    private final GetCrewMemberUseCase getCrewMemberUseCase;
    private final ListCrewMembersUseCase listCrewMembersUseCase;
    private final CrewMemberResponseMapper responseMapper;

    @PostMapping
    public ResponseEntity<CrewMemberResponse> create(@AuthenticationPrincipal String promoterId,
                                                       @Valid @RequestBody CreateCrewMemberRequest request) {
        var command = new CreateCrewMemberCommand(promoterId, request.name(), request.defaultRole(),
                request.contactPhone(), request.contactEmail());
        var crewMember = createCrewMemberUseCase.execute(command);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseMapper.toResponse(crewMember));
    }

    @GetMapping("/{id}")
    public CrewMemberResponse get(@AuthenticationPrincipal String promoterId, @PathVariable String id) {
        return responseMapper.toResponse(getCrewMemberUseCase.execute(new GetCrewMemberQuery(id, promoterId)));
    }

    @GetMapping
    public List<CrewMemberResponse> list(@AuthenticationPrincipal String promoterId,
                                          @RequestParam(defaultValue = "false") boolean activeOnly) {
        return listCrewMembersUseCase.execute(new ListCrewMembersQuery(promoterId, activeOnly)).stream()
                .map(responseMapper::toResponse).toList();
    }

    @PutMapping("/{id}")
    public CrewMemberResponse update(@AuthenticationPrincipal String promoterId, @PathVariable String id,
                                      @Valid @RequestBody UpdateCrewMemberRequest request) {
        var command = new UpdateCrewMemberCommand(id, promoterId, request.name(), request.defaultRole(),
                request.contactPhone(), request.contactEmail(), request.documents());
        return responseMapper.toResponse(updateCrewMemberUseCase.execute(command));
    }

    @PostMapping("/{id}/deactivate")
    public CrewMemberResponse deactivate(@AuthenticationPrincipal String promoterId, @PathVariable String id) {
        return responseMapper.toResponse(deactivateCrewMemberUseCase.execute(new DeactivateCrewMemberCommand(id, promoterId)));
    }
}

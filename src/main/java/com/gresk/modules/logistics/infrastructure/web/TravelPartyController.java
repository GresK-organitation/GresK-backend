package com.gresk.modules.logistics.infrastructure.web;

import com.gresk.modules.logistics.application.command.CreateTravelPartyCommand;
import com.gresk.modules.logistics.application.command.UpdateTravelPartyMembersCommand;
import com.gresk.modules.logistics.application.dto.TravelPartyResponse;
import com.gresk.modules.logistics.application.dto.TravelPartyResponseMapper;
import com.gresk.modules.logistics.application.port.in.CreateTravelPartyUseCase;
import com.gresk.modules.logistics.application.port.in.GetTravelPartyByTourUseCase;
import com.gresk.modules.logistics.application.port.in.UpdateTravelPartyMembersUseCase;
import com.gresk.modules.logistics.application.query.GetTravelPartyByTourQuery;
import com.gresk.modules.logistics.infrastructure.web.dto.CreateTravelPartyRequest;
import com.gresk.modules.logistics.infrastructure.web.dto.UpdateTravelPartyMembersRequest;
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
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/logistics/travel-parties")
@RequiredArgsConstructor
@PreAuthorize("hasRole('PROMOTER')")
@Tag(name = "Logistics - Travel Party", description = "Roster de expedición (músicos + crew) de un Tour")
public class TravelPartyController {

    private final CreateTravelPartyUseCase createTravelPartyUseCase;
    private final UpdateTravelPartyMembersUseCase updateTravelPartyMembersUseCase;
    private final GetTravelPartyByTourUseCase getTravelPartyByTourUseCase;
    private final TravelPartyResponseMapper responseMapper;

    @PostMapping
    public ResponseEntity<TravelPartyResponse> create(@AuthenticationPrincipal String promoterId,
                                                        @Valid @RequestBody CreateTravelPartyRequest request) {
        var travelParty = createTravelPartyUseCase.execute(new CreateTravelPartyCommand(request.tourId(), promoterId));
        return ResponseEntity.status(HttpStatus.CREATED).body(responseMapper.toResponse(travelParty));
    }

    @GetMapping("/by-tour/{tourId}")
    public TravelPartyResponse getByTour(@AuthenticationPrincipal String promoterId, @PathVariable String tourId) {
        return responseMapper.toResponse(getTravelPartyByTourUseCase.execute(new GetTravelPartyByTourQuery(tourId, promoterId)));
    }

    @PutMapping("/{id}/members")
    public TravelPartyResponse updateMembers(@AuthenticationPrincipal String promoterId, @PathVariable String id,
                                              @RequestBody UpdateTravelPartyMembersRequest request) {
        var command = new UpdateTravelPartyMembersCommand(id, promoterId, request.members());
        return responseMapper.toResponse(updateTravelPartyMembersUseCase.execute(command));
    }
}

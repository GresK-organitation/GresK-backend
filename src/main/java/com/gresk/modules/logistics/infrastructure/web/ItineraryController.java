package com.gresk.modules.logistics.infrastructure.web;

import com.gresk.modules.logistics.application.command.CreateItineraryCommand;
import com.gresk.modules.logistics.application.command.UpdateItinerarySegmentsCommand;
import com.gresk.modules.logistics.application.dto.ItineraryResponse;
import com.gresk.modules.logistics.application.dto.ItineraryResponseMapper;
import com.gresk.modules.logistics.application.port.in.CreateItineraryUseCase;
import com.gresk.modules.logistics.application.port.in.GetItineraryByTourUseCase;
import com.gresk.modules.logistics.application.port.in.UpdateItinerarySegmentsUseCase;
import com.gresk.modules.logistics.application.query.GetItineraryByTourQuery;
import com.gresk.modules.logistics.infrastructure.web.dto.CreateItineraryRequest;
import com.gresk.modules.logistics.infrastructure.web.dto.UpdateItinerarySegmentsRequest;
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
@RequestMapping("/api/v1/logistics/itineraries")
@RequiredArgsConstructor
@PreAuthorize("hasRole('PROMOTER')")
@Tag(name = "Logistics - Itinerary", description = "Tramos de transporte (vuelos, trenes, transfers) de un Tour")
public class ItineraryController {

    private final CreateItineraryUseCase createItineraryUseCase;
    private final UpdateItinerarySegmentsUseCase updateItinerarySegmentsUseCase;
    private final GetItineraryByTourUseCase getItineraryByTourUseCase;
    private final ItineraryResponseMapper responseMapper;

    @PostMapping
    public ResponseEntity<ItineraryResponse> create(@AuthenticationPrincipal String promoterId,
                                                      @Valid @RequestBody CreateItineraryRequest request) {
        var itinerary = createItineraryUseCase.execute(new CreateItineraryCommand(request.tourId(), promoterId));
        return ResponseEntity.status(HttpStatus.CREATED).body(responseMapper.toResponse(itinerary));
    }

    @GetMapping("/by-tour/{tourId}")
    public ItineraryResponse getByTour(@AuthenticationPrincipal String promoterId, @PathVariable String tourId) {
        return responseMapper.toResponse(getItineraryByTourUseCase.execute(new GetItineraryByTourQuery(tourId, promoterId)));
    }

    @PutMapping("/{id}/segments")
    public ItineraryResponse updateSegments(@AuthenticationPrincipal String promoterId, @PathVariable String id,
                                             @RequestBody UpdateItinerarySegmentsRequest request) {
        var command = new UpdateItinerarySegmentsCommand(id, promoterId, request.segments());
        return responseMapper.toResponse(updateItinerarySegmentsUseCase.execute(command));
    }
}

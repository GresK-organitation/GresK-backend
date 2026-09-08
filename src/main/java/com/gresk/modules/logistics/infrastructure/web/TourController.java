package com.gresk.modules.logistics.infrastructure.web;

import com.gresk.modules.logistics.application.command.ChangeTourStatusCommand;
import com.gresk.modules.logistics.application.command.CreateTourCommand;
import com.gresk.modules.logistics.application.command.UpdateTourDetailsCommand;
import com.gresk.modules.logistics.application.command.UpdateTourLegsCommand;
import com.gresk.modules.logistics.application.command.UpdateTourReferenceDataCommand;
import com.gresk.modules.logistics.application.dto.TourBookResponse;
import com.gresk.modules.logistics.application.dto.TourResponse;
import com.gresk.modules.logistics.application.dto.TourResponseMapper;
import com.gresk.modules.logistics.application.port.in.ChangeTourStatusUseCase;
import com.gresk.modules.logistics.application.port.in.CreateTourUseCase;
import com.gresk.modules.logistics.application.port.in.GenerateTourBookUseCase;
import com.gresk.modules.logistics.application.port.in.GetTourUseCase;
import com.gresk.modules.logistics.application.port.in.ListToursUseCase;
import com.gresk.modules.logistics.application.port.in.UpdateTourDetailsUseCase;
import com.gresk.modules.logistics.application.port.in.UpdateTourLegsUseCase;
import com.gresk.modules.logistics.application.port.in.UpdateTourReferenceDataUseCase;
import com.gresk.modules.logistics.application.query.GenerateTourBookQuery;
import com.gresk.modules.logistics.application.query.GetTourQuery;
import com.gresk.modules.logistics.application.query.ListToursQuery;
import com.gresk.modules.logistics.infrastructure.web.dto.ChangeTourStatusRequest;
import com.gresk.modules.logistics.infrastructure.web.dto.CreateTourRequest;
import com.gresk.modules.logistics.infrastructure.web.dto.UpdateTourDetailsRequest;
import com.gresk.modules.logistics.infrastructure.web.dto.UpdateTourLegsRequest;
import com.gresk.modules.logistics.infrastructure.web.dto.UpdateTourReferenceDataRequest;
import com.gresk.modules.logistics.infrastructure.web.pdf.TourBookPdfRenderer;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/logistics/tours")
@RequiredArgsConstructor
@PreAuthorize("hasRole('PROMOTER')")
@Tag(name = "Logistics - Tours", description = "Giras (una o varias fechas), sus legs, contactos de emergencia, POIs y Tour Book")
public class TourController {

    private final CreateTourUseCase createTourUseCase;
    private final UpdateTourDetailsUseCase updateTourDetailsUseCase;
    private final UpdateTourLegsUseCase updateTourLegsUseCase;
    private final UpdateTourReferenceDataUseCase updateTourReferenceDataUseCase;
    private final ChangeTourStatusUseCase changeTourStatusUseCase;
    private final GetTourUseCase getTourUseCase;
    private final ListToursUseCase listToursUseCase;
    private final GenerateTourBookUseCase generateTourBookUseCase;
    private final TourResponseMapper responseMapper;
    private final TourBookPdfRenderer tourBookPdfRenderer;

    @PostMapping
    public ResponseEntity<TourResponse> create(@AuthenticationPrincipal String promoterId,
                                                @Valid @RequestBody CreateTourRequest request) {
        var command = new CreateTourCommand(promoterId, request.artistId(), request.name(), request.startDate(),
                request.endDate(), request.notes(), request.legs());
        var tour = createTourUseCase.execute(command);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseMapper.toResponse(tour));
    }

    @GetMapping("/{id}")
    public TourResponse get(@AuthenticationPrincipal String promoterId, @PathVariable String id) {
        return responseMapper.toResponse(getTourUseCase.execute(new GetTourQuery(id, promoterId)));
    }

    @GetMapping
    public List<TourResponse> list(@AuthenticationPrincipal String promoterId,
                                    @RequestParam(required = false) String status) {
        return listToursUseCase.execute(new ListToursQuery(promoterId, status)).stream()
                .map(responseMapper::toResponse).toList();
    }

    @PutMapping("/{id}")
    public TourResponse updateDetails(@AuthenticationPrincipal String promoterId, @PathVariable String id,
                                       @Valid @RequestBody UpdateTourDetailsRequest request) {
        var command = new UpdateTourDetailsCommand(id, promoterId, request.name(), request.startDate(),
                request.endDate(), request.notes());
        return responseMapper.toResponse(updateTourDetailsUseCase.execute(command));
    }

    @PutMapping("/{id}/legs")
    public TourResponse updateLegs(@AuthenticationPrincipal String promoterId, @PathVariable String id,
                                    @RequestBody UpdateTourLegsRequest request) {
        var command = new UpdateTourLegsCommand(id, promoterId, request.legs());
        return responseMapper.toResponse(updateTourLegsUseCase.execute(command));
    }

    @PutMapping("/{id}/reference-data")
    public TourResponse updateReferenceData(@AuthenticationPrincipal String promoterId, @PathVariable String id,
                                             @RequestBody UpdateTourReferenceDataRequest request) {
        var command = new UpdateTourReferenceDataCommand(id, promoterId, request.emergencyContacts(),
                request.pointsOfInterest());
        return responseMapper.toResponse(updateTourReferenceDataUseCase.execute(command));
    }

    @PatchMapping("/{id}/status")
    public TourResponse changeStatus(@AuthenticationPrincipal String promoterId, @PathVariable String id,
                                      @Valid @RequestBody ChangeTourStatusRequest request) {
        var command = new ChangeTourStatusCommand(id, promoterId, request.targetStatus(), request.reason());
        return responseMapper.toResponse(changeTourStatusUseCase.execute(command));
    }

    @GetMapping("/{id}/tour-book")
    public TourBookResponse tourBook(@AuthenticationPrincipal String promoterId, @PathVariable String id) {
        return generateTourBookUseCase.execute(new GenerateTourBookQuery(id, promoterId));
    }

    @GetMapping(value = "/{id}/tour-book/pdf", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<byte[]> tourBookPdf(@AuthenticationPrincipal String promoterId, @PathVariable String id) {
        var tourBook = generateTourBookUseCase.execute(new GenerateTourBookQuery(id, promoterId));
        byte[] pdf = tourBookPdfRenderer.render(tourBook);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"tour-book-" + id + ".pdf\"")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }
}

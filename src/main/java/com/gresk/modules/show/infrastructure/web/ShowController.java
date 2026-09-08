package com.gresk.modules.show.infrastructure.web;

import com.gresk.modules.show.application.command.AddLogEntryCommand;
import com.gresk.modules.show.application.command.CancelShowCommand;
import com.gresk.modules.show.application.command.CreateShowCommand;
import com.gresk.modules.show.application.command.OpenSalesCommand;
import com.gresk.modules.show.application.command.PlaceHoldCommand;
import com.gresk.modules.show.application.command.RunViabilitySimulationCommand;
import com.gresk.modules.show.application.command.SelectVenueCommand;
import com.gresk.modules.show.application.command.SettleShowCommand;
import com.gresk.modules.show.application.command.UpdateShowDetailsCommand;
import com.gresk.modules.show.application.port.in.AddLogEntryUseCase;
import com.gresk.modules.show.application.port.in.CancelShowUseCase;
import com.gresk.modules.show.application.port.in.ConfirmShowUseCase;
import com.gresk.modules.show.application.port.in.CreateShowUseCase;
import com.gresk.modules.show.application.port.in.FinishShowUseCase;
import com.gresk.modules.show.application.port.in.GetShowTimelineUseCase;
import com.gresk.modules.show.application.port.in.GetShowUseCase;
import com.gresk.modules.show.application.port.in.ListShowsUseCase;
import com.gresk.modules.show.application.port.in.OpenSalesUseCase;
import com.gresk.modules.show.application.port.in.PlaceHoldUseCase;
import com.gresk.modules.show.application.port.in.RunViabilitySimulationUseCase;
import com.gresk.modules.show.application.port.in.SelectVenueUseCase;
import com.gresk.modules.show.application.port.in.SettleShowUseCase;
import com.gresk.modules.show.application.port.in.StartExecutionUseCase;
import com.gresk.modules.show.application.port.in.UpdateShowDetailsUseCase;
import com.gresk.modules.show.application.query.ListShowsQuery;
import com.gresk.modules.show.domain.model.ShowStatus;
import com.gresk.modules.show.infrastructure.web.dto.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/v1/shows")
@RequiredArgsConstructor
@PreAuthorize("hasRole('PROMOTER')")
public class ShowController {

    private final CreateShowUseCase createShowUseCase;
    private final UpdateShowDetailsUseCase updateShowDetailsUseCase;
    private final SelectVenueUseCase selectVenueUseCase;
    private final RunViabilitySimulationUseCase runViabilitySimulationUseCase;
    private final PlaceHoldUseCase placeHoldUseCase;
    private final ConfirmShowUseCase confirmShowUseCase;
    private final OpenSalesUseCase openSalesUseCase;
    private final StartExecutionUseCase startExecutionUseCase;
    private final FinishShowUseCase finishShowUseCase;
    private final SettleShowUseCase settleShowUseCase;
    private final CancelShowUseCase cancelShowUseCase;
    private final AddLogEntryUseCase addLogEntryUseCase;
    private final GetShowUseCase getShowUseCase;
    private final ListShowsUseCase listShowsUseCase;
    private final GetShowTimelineUseCase getShowTimelineUseCase;
    private final ShowResponseMapper mapper;

    @PostMapping
    public ResponseEntity<ShowResponse> create(@AuthenticationPrincipal String promoterId,
                                                @Valid @RequestBody CreateShowRequest request) {
        var show = createShowUseCase.execute(new CreateShowCommand(promoterId, request.name(), request.tentativeDate()));
        return ResponseEntity.created(URI.create("/api/v1/shows/" + show.getId())).body(mapper.toResponse(show));
    }

    @GetMapping
    public List<ShowResponse> list(@AuthenticationPrincipal String promoterId,
                                    @RequestParam(required = false) ShowStatus status) {
        return listShowsUseCase.execute(new ListShowsQuery(promoterId, status)).stream()
                .map(mapper::toResponse).toList();
    }

    @GetMapping("/{showId}")
    public ShowResponse getById(@AuthenticationPrincipal String promoterId, @PathVariable String showId) {
        return mapper.toResponse(getShowUseCase.execute(showId, promoterId));
    }

    @PutMapping("/{showId}")
    public ShowResponse updateDetails(@AuthenticationPrincipal String promoterId, @PathVariable String showId,
                                       @Valid @RequestBody UpdateShowDetailsRequest request) {
        var command = new UpdateShowDetailsCommand(showId, promoterId, request.name(), request.scheduledDate());
        return mapper.toResponse(updateShowDetailsUseCase.execute(command));
    }

    @PutMapping("/{showId}/venue")
    public ShowResponse selectVenue(@AuthenticationPrincipal String promoterId, @PathVariable String showId,
                                     @Valid @RequestBody SelectVenueRequest request) {
        var command = new SelectVenueCommand(showId, promoterId, request.venueId(), request.capacityConfigCode());
        return mapper.toResponse(selectVenueUseCase.execute(command));
    }

    // ── Simulador de viabilidad (P&L Borrador) ────────────────────────────────
    @PostMapping("/{showId}/simulate")
    public FinancialViabilityReportResponse simulate(@AuthenticationPrincipal String promoterId, @PathVariable String showId,
                                                       @Valid @RequestBody RunViabilitySimulationRequest request) {
        var items = request.costLineItems() == null ? List.<com.gresk.modules.show.application.command.CostLineItemInput>of()
                : request.costLineItems().stream()
                        .map(i -> new com.gresk.modules.show.application.command.CostLineItemInput(
                                i.category(), i.label(), i.amount(), i.nature()))
                        .toList();
        var command = new RunViabilitySimulationCommand(showId, promoterId, items,
                request.avgTicketPrice(), request.expectedSelloutPercent(), request.currency());
        return mapper.toResponse(runViabilitySimulationUseCase.execute(command));
    }

    // ── Máquina de estados ─────────────────────────────────────────────────────
    @PostMapping("/{showId}/hold")
    public ShowResponse placeHold(@AuthenticationPrincipal String promoterId, @PathVariable String showId,
                                   @Valid @RequestBody PlaceHoldRequest request) {
        return mapper.toResponse(placeHoldUseCase.execute(
                new PlaceHoldCommand(showId, promoterId, request.holdDurationHours())));
    }

    @PostMapping("/{showId}/confirm")
    public ShowResponse confirm(@AuthenticationPrincipal String promoterId, @PathVariable String showId) {
        return mapper.toResponse(confirmShowUseCase.execute(showId, promoterId));
    }

    @PostMapping("/{showId}/open-sales")
    public ShowResponse openSales(@AuthenticationPrincipal String promoterId, @PathVariable String showId,
                                   @Valid @RequestBody OpenSalesRequest request) {
        return mapper.toResponse(openSalesUseCase.execute(new OpenSalesCommand(showId, promoterId, request.genre())));
    }

    @PostMapping("/{showId}/start-execution")
    public ShowResponse startExecution(@AuthenticationPrincipal String promoterId, @PathVariable String showId) {
        return mapper.toResponse(startExecutionUseCase.execute(showId, promoterId));
    }

    @PostMapping("/{showId}/finish")
    public ShowResponse finish(@AuthenticationPrincipal String promoterId, @PathVariable String showId) {
        return mapper.toResponse(finishShowUseCase.execute(showId, promoterId));
    }

    @PostMapping("/{showId}/settle")
    public ShowResponse settle(@AuthenticationPrincipal String promoterId, @PathVariable String showId,
                                @Valid @RequestBody SettleShowRequest request) {
        var command = new SettleShowCommand(showId, promoterId,
                request.actualAttendance(), request.actualRevenue(), request.actualCosts());
        return mapper.toResponse(settleShowUseCase.execute(command));
    }

    @PostMapping("/{showId}/cancel")
    public ShowResponse cancel(@AuthenticationPrincipal String promoterId, @PathVariable String showId,
                                @RequestBody(required = false) CancelShowRequest request) {
        String reason = request == null ? null : request.reason();
        return mapper.toResponse(cancelShowUseCase.execute(new CancelShowCommand(showId, promoterId, reason)));
    }

    // ── Bitácora ─────────────────────────────────────────────────────────────
    @GetMapping("/{showId}/timeline")
    public List<ShowLogEntryResponse> timeline(@AuthenticationPrincipal String promoterId, @PathVariable String showId) {
        return getShowTimelineUseCase.execute(showId, promoterId).stream().map(mapper::toResponse).toList();
    }

    @PostMapping("/{showId}/timeline")
    @ResponseStatus(HttpStatus.CREATED)
    public ShowLogEntryResponse addLogEntry(@AuthenticationPrincipal String promoterId, @PathVariable String showId,
                                             @Valid @RequestBody AddLogEntryRequest request) {
        var command = new AddLogEntryCommand(showId, promoterId, request.type(), request.actor(),
                request.description(), request.relatedParty(), request.attachmentAssetIds());
        return mapper.toResponse(addLogEntryUseCase.execute(command));
    }
}

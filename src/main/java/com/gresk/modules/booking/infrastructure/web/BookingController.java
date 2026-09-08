package com.gresk.modules.booking.infrastructure.web;

import com.gresk.modules.booking.application.command.CancelBookingCommand;
import com.gresk.modules.booking.application.command.CompleteMilestoneCommand;
import com.gresk.modules.booking.application.command.CreateHoldCommand;
import com.gresk.modules.booking.application.command.PromoteHoldCommand;
import com.gresk.modules.booking.application.command.RescheduleBookingCommand;
import com.gresk.modules.booking.application.command.SkipMilestoneCommand;
import com.gresk.modules.booking.application.command.UpdateDaySheetCommand;
import com.gresk.modules.booking.application.dto.BookingResponse;
import com.gresk.modules.booking.application.dto.BookingResponseMapper;
import com.gresk.modules.booking.application.dto.DaySheetResponse;
import com.gresk.modules.booking.application.dto.TerritorialConflictResponse;
import com.gresk.modules.booking.application.dto.TimelineEntryResponse;
import com.gresk.modules.booking.application.dto.VenueGanttRowResponse;
import com.gresk.modules.booking.application.port.in.CancelBookingUseCase;
import com.gresk.modules.booking.application.port.in.CheckTerritorialExclusivityUseCase;
import com.gresk.modules.booking.application.port.in.CompleteMilestoneUseCase;
import com.gresk.modules.booking.application.port.in.CreateHoldUseCase;
import com.gresk.modules.booking.application.port.in.GetArtistTimelineUseCase;
import com.gresk.modules.booking.application.port.in.GetBookingUseCase;
import com.gresk.modules.booking.application.port.in.GetDaySheetUseCase;
import com.gresk.modules.booking.application.port.in.GetVenueGanttUseCase;
import com.gresk.modules.booking.application.port.in.ListBookingsUseCase;
import com.gresk.modules.booking.application.port.in.PromoteHoldUseCase;
import com.gresk.modules.booking.application.port.in.RescheduleBookingUseCase;
import com.gresk.modules.booking.application.port.in.SkipMilestoneUseCase;
import com.gresk.modules.booking.application.port.in.UpdateDaySheetUseCase;
import com.gresk.modules.booking.application.query.ArtistTimelineQuery;
import com.gresk.modules.booking.application.query.CheckTerritorialExclusivityQuery;
import com.gresk.modules.booking.application.query.GetBookingQuery;
import com.gresk.modules.booking.application.query.GetDaySheetQuery;
import com.gresk.modules.booking.application.query.ListBookingsQuery;
import com.gresk.modules.booking.application.query.VenueGanttQuery;
import com.gresk.modules.booking.infrastructure.web.dto.CreateHoldRequest;
import com.gresk.modules.booking.infrastructure.web.dto.NotesRequest;
import com.gresk.modules.booking.infrastructure.web.dto.PromoteHoldRequest;
import com.gresk.modules.booking.infrastructure.web.dto.ReasonRequest;
import com.gresk.modules.booking.infrastructure.web.dto.RescheduleBookingRequest;
import com.gresk.modules.booking.infrastructure.web.dto.TerritorialExclusivityCheckRequest;
import com.gresk.modules.booking.infrastructure.web.dto.UpdateDaySheetRequest;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.List;

@RestController
@RequestMapping("/api/v1/bookings")
@RequiredArgsConstructor
@PreAuthorize("hasRole('PROMOTER')")
@Tag(name = "Booking", description = "Sistema de holds, hitos automáticos, exclusividad territorial y vistas Gantt/Timeline/Day Sheet")
public class BookingController {

    private final CreateHoldUseCase createHoldUseCase;
    private final PromoteHoldUseCase promoteHoldUseCase;
    private final CancelBookingUseCase cancelBookingUseCase;
    private final RescheduleBookingUseCase rescheduleBookingUseCase;
    private final CompleteMilestoneUseCase completeMilestoneUseCase;
    private final SkipMilestoneUseCase skipMilestoneUseCase;
    private final GetBookingUseCase getBookingUseCase;
    private final ListBookingsUseCase listBookingsUseCase;
    private final CheckTerritorialExclusivityUseCase checkTerritorialExclusivityUseCase;
    private final GetVenueGanttUseCase getVenueGanttUseCase;
    private final GetArtistTimelineUseCase getArtistTimelineUseCase;
    private final GetDaySheetUseCase getDaySheetUseCase;
    private final UpdateDaySheetUseCase updateDaySheetUseCase;
    private final BookingResponseMapper responseMapper;

    @PostMapping("/holds")
    public ResponseEntity<BookingResponse> createHold(@AuthenticationPrincipal String promoterId,
                                                        @Valid @RequestBody CreateHoldRequest request) {
        var command = new CreateHoldCommand(promoterId, request.artistId(), request.venue(), request.eventDate(),
                request.holdLevel(), request.holdExpiresAt(), request.milestoneBlueprints(), request.exclusivity(),
                request.notes(), request.forceIgnoreConflicts());
        var booking = createHoldUseCase.execute(command);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseMapper.toResponse(booking));
    }

    @GetMapping("/{id}")
    public BookingResponse getBooking(@AuthenticationPrincipal String promoterId, @PathVariable String id) {
        return responseMapper.toResponse(getBookingUseCase.execute(new GetBookingQuery(id, promoterId)));
    }

    @GetMapping
    public List<BookingResponse> listBookings(@AuthenticationPrincipal String promoterId,
                                               @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant from,
                                               @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant to,
                                               @RequestParam(required = false) String status) {
        return listBookingsUseCase.execute(new ListBookingsQuery(promoterId, from, to, status)).stream()
                .map(responseMapper::toResponse).toList();
    }

    @PatchMapping("/{id}/promote")
    public BookingResponse promote(@AuthenticationPrincipal String promoterId, @PathVariable String id,
                                    @Valid @RequestBody PromoteHoldRequest request) {
        var command = new PromoteHoldCommand(id, promoterId, request.targetStatus(), request.newHoldExpiresAt());
        return responseMapper.toResponse(promoteHoldUseCase.execute(command));
    }

    @PostMapping("/{id}/cancel")
    public BookingResponse cancel(@AuthenticationPrincipal String promoterId, @PathVariable String id,
                                   @RequestBody(required = false) ReasonRequest request) {
        String reason = request == null ? null : request.reason();
        return responseMapper.toResponse(cancelBookingUseCase.execute(new CancelBookingCommand(id, promoterId, reason)));
    }

    @PatchMapping("/{id}/reschedule")
    public BookingResponse reschedule(@AuthenticationPrincipal String promoterId, @PathVariable String id,
                                       @Valid @RequestBody RescheduleBookingRequest request) {
        var command = new RescheduleBookingCommand(id, promoterId, request.newEventDate(), request.reason());
        return responseMapper.toResponse(rescheduleBookingUseCase.execute(command));
    }

    @PatchMapping("/{id}/milestones/{milestoneId}/complete")
    public BookingResponse completeMilestone(@AuthenticationPrincipal String promoterId, @PathVariable String id,
                                              @PathVariable String milestoneId,
                                              @RequestBody(required = false) NotesRequest request) {
        String notes = request == null ? null : request.notes();
        var command = new CompleteMilestoneCommand(id, milestoneId, promoterId, notes);
        return responseMapper.toResponse(completeMilestoneUseCase.execute(command));
    }

    @PatchMapping("/{id}/milestones/{milestoneId}/skip")
    public BookingResponse skipMilestone(@AuthenticationPrincipal String promoterId, @PathVariable String id,
                                          @PathVariable String milestoneId,
                                          @RequestBody(required = false) ReasonRequest request) {
        String reason = request == null ? null : request.reason();
        var command = new SkipMilestoneCommand(id, milestoneId, promoterId, reason);
        return responseMapper.toResponse(skipMilestoneUseCase.execute(command));
    }

    @PostMapping("/exclusivity/check")
    public List<TerritorialConflictResponse> checkExclusivity(@AuthenticationPrincipal String promoterId,
                                                                @Valid @RequestBody TerritorialExclusivityCheckRequest request) {
        var query = new CheckTerritorialExclusivityQuery(promoterId, request.artistId(), request.venue(),
                request.eventDate(), request.daysBefore(), request.daysAfter(), request.excludeBookingId());
        return checkTerritorialExclusivityUseCase.execute(query).stream().map(responseMapper::toResponse).toList();
    }

    @GetMapping("/venues/gantt")
    public List<VenueGanttRowResponse> venueGantt(@AuthenticationPrincipal String promoterId,
                                                    @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant from,
                                                    @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant to,
                                                    @RequestParam(required = false) String venueId) {
        return getVenueGanttUseCase.execute(new VenueGanttQuery(promoterId, from, to, venueId)).stream()
                .map(responseMapper::toResponse).toList();
    }

    @GetMapping("/artists/{artistId}/timeline")
    public List<TimelineEntryResponse> artistTimeline(@AuthenticationPrincipal String promoterId,
                                                        @PathVariable String artistId,
                                                        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant from,
                                                        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant to) {
        return getArtistTimelineUseCase.execute(new ArtistTimelineQuery(promoterId, artistId, from, to)).stream()
                .map(responseMapper::toResponse).toList();
    }

    @GetMapping("/{id}/day-sheet")
    public DaySheetResponse getDaySheet(@AuthenticationPrincipal String promoterId, @PathVariable String id) {
        return responseMapper.toResponse(getDaySheetUseCase.execute(new GetDaySheetQuery(id, promoterId)));
    }

    @PutMapping("/{id}/day-sheet")
    public BookingResponse updateDaySheet(@AuthenticationPrincipal String promoterId, @PathVariable String id,
                                           @Valid @RequestBody UpdateDaySheetRequest request) {
        var command = new UpdateDaySheetCommand(id, promoterId, request.showDate(), request.entries());
        return responseMapper.toResponse(updateDaySheetUseCase.execute(command));
    }
}

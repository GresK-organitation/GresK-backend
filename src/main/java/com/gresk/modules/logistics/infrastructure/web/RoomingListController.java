package com.gresk.modules.logistics.infrastructure.web;

import com.gresk.modules.logistics.application.command.CreateRoomingListCommand;
import com.gresk.modules.logistics.application.command.GenerateRoomAssignmentsCommand;
import com.gresk.modules.logistics.application.command.UpdateRoomAssignmentsCommand;
import com.gresk.modules.logistics.application.command.UpdateRoomingListCommand;
import com.gresk.modules.logistics.application.dto.RoomingListResponse;
import com.gresk.modules.logistics.application.dto.RoomingListResponseMapper;
import com.gresk.modules.logistics.application.port.in.CreateRoomingListUseCase;
import com.gresk.modules.logistics.application.port.in.GenerateRoomAssignmentsUseCase;
import com.gresk.modules.logistics.application.port.in.GetRoomingListUseCase;
import com.gresk.modules.logistics.application.port.in.ListRoomingListsByTourUseCase;
import com.gresk.modules.logistics.application.port.in.UpdateRoomAssignmentsUseCase;
import com.gresk.modules.logistics.application.port.in.UpdateRoomingListUseCase;
import com.gresk.modules.logistics.application.query.GetRoomingListQuery;
import com.gresk.modules.logistics.application.query.ListRoomingListsByTourQuery;
import com.gresk.modules.logistics.domain.model.valueobject.RoomAssignment;
import com.gresk.modules.logistics.infrastructure.web.dto.CreateRoomingListRequest;
import com.gresk.modules.logistics.infrastructure.web.dto.UpdateRoomAssignmentsRequest;
import com.gresk.modules.logistics.infrastructure.web.dto.UpdateRoomingListRequest;
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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.nio.charset.StandardCharsets;
import java.util.List;

@RestController
@RequestMapping("/api/v1/logistics/rooming-lists")
@RequiredArgsConstructor
@PreAuthorize("hasRole('PROMOTER')")
@Tag(name = "Logistics - Rooming List", description = "Bloque de hotel de un Tour y su reparto de habitaciones")
public class RoomingListController {

    private final CreateRoomingListUseCase createRoomingListUseCase;
    private final UpdateRoomingListUseCase updateRoomingListUseCase;
    private final GenerateRoomAssignmentsUseCase generateRoomAssignmentsUseCase;
    private final UpdateRoomAssignmentsUseCase updateRoomAssignmentsUseCase;
    private final GetRoomingListUseCase getRoomingListUseCase;
    private final ListRoomingListsByTourUseCase listRoomingListsByTourUseCase;
    private final RoomingListResponseMapper responseMapper;

    @PostMapping
    public ResponseEntity<RoomingListResponse> create(@AuthenticationPrincipal String promoterId,
                                                        @Valid @RequestBody CreateRoomingListRequest request) {
        var command = new CreateRoomingListCommand(request.tourId(), promoterId, request.hotelName(),
                request.hotelAddress(), request.checkInDate(), request.checkOutDate(), request.allotments());
        var roomingList = createRoomingListUseCase.execute(command);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseMapper.toResponse(roomingList));
    }

    @GetMapping("/{id}")
    public RoomingListResponse get(@AuthenticationPrincipal String promoterId, @PathVariable String id) {
        return responseMapper.toResponse(getRoomingListUseCase.execute(new GetRoomingListQuery(id, promoterId)));
    }

    @GetMapping("/by-tour/{tourId}")
    public List<RoomingListResponse> listByTour(@AuthenticationPrincipal String promoterId, @PathVariable String tourId) {
        return listRoomingListsByTourUseCase.execute(new ListRoomingListsByTourQuery(tourId, promoterId)).stream()
                .map(responseMapper::toResponse).toList();
    }

    @PutMapping("/{id}")
    public RoomingListResponse update(@AuthenticationPrincipal String promoterId, @PathVariable String id,
                                       @Valid @RequestBody UpdateRoomingListRequest request) {
        var command = new UpdateRoomingListCommand(id, promoterId, request.hotelName(), request.hotelAddress(),
                request.checkInDate(), request.checkOutDate(), request.allotments());
        return responseMapper.toResponse(updateRoomingListUseCase.execute(command));
    }

    @PostMapping("/{id}/generate-assignments")
    public RoomingListResponse generateAssignments(@AuthenticationPrincipal String promoterId, @PathVariable String id) {
        var roomingList = generateRoomAssignmentsUseCase.execute(new GenerateRoomAssignmentsCommand(id, promoterId));
        return responseMapper.toResponse(roomingList);
    }

    @PutMapping("/{id}/assignments")
    public RoomingListResponse updateAssignments(@AuthenticationPrincipal String promoterId, @PathVariable String id,
                                                  @RequestBody UpdateRoomAssignmentsRequest request) {
        var command = new UpdateRoomAssignmentsCommand(id, promoterId, request.assignments());
        return responseMapper.toResponse(updateRoomAssignmentsUseCase.execute(command));
    }

    /** CSV plano (habitación, tipo, ocupantes) en un formato que cualquier recepción de hotel puede importar. */
    @GetMapping(value = "/{id}/export.csv", produces = "text/csv")
    public ResponseEntity<byte[]> exportCsv(@AuthenticationPrincipal String promoterId, @PathVariable String id) {
        var roomingList = getRoomingListUseCase.execute(new GetRoomingListQuery(id, promoterId));
        StringBuilder csv = new StringBuilder("Room Type,Room Number,Occupant Count,Occupant IDs\n");
        for (RoomAssignment a : roomingList.getAssignments()) {
            csv.append(a.roomType()).append(',')
                    .append(a.roomNumber() == null ? "" : a.roomNumber()).append(',')
                    .append(a.occupantIds().size()).append(',')
                    .append(String.join(";", a.occupantIds().stream().map(Object::toString).toList()))
                    .append('\n');
        }
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"rooming-list-" + id + ".csv\"")
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(csv.toString().getBytes(StandardCharsets.UTF_8));
    }
}

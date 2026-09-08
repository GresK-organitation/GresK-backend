package com.gresk.modules.logistics.application.usecase;

import com.gresk.modules.logistics.application.command.GenerateRoomAssignmentsCommand;
import com.gresk.modules.logistics.application.port.in.GenerateRoomAssignmentsUseCase;
import com.gresk.modules.logistics.domain.model.RoomingList;
import com.gresk.modules.logistics.domain.model.TravelParty;
import com.gresk.modules.logistics.domain.model.valueobject.RoomAssignment;
import com.gresk.modules.logistics.domain.port.out.RoomingListRepositoryPort;
import com.gresk.modules.logistics.domain.port.out.TravelPartyRepositoryPort;
import com.gresk.modules.logistics.domain.service.RoomingListGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class GenerateRoomAssignmentsService implements GenerateRoomAssignmentsUseCase {

    private final RoomingListRepositoryPort roomingListRepository;
    private final TravelPartyRepositoryPort travelPartyRepository;

    @Override
    public RoomingList execute(GenerateRoomAssignmentsCommand command) {
        RoomingList roomingList = LogisticsLookups.requireRoomingListById(roomingListRepository, command.roomingListId(), command.promoterId());
        TravelParty travelParty = LogisticsLookups.requireTravelPartyByTour(travelPartyRepository, roomingList.getTourId(), command.promoterId());

        List<RoomAssignment> generated = RoomingListGenerator.generate(travelParty.getActiveMembers(), roomingList.getAllotments());
        roomingList.applyAssignments(generated);
        return roomingListRepository.save(roomingList);
    }
}

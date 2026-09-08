package com.gresk.modules.logistics.application.usecase;

import com.gresk.modules.logistics.application.command.UpdateRoomAssignmentsCommand;
import com.gresk.modules.logistics.application.port.in.UpdateRoomAssignmentsUseCase;
import com.gresk.modules.logistics.domain.model.RoomingList;
import com.gresk.modules.logistics.domain.port.out.RoomingListRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class UpdateRoomAssignmentsService implements UpdateRoomAssignmentsUseCase {

    private final RoomingListRepositoryPort roomingListRepository;

    @Override
    public RoomingList execute(UpdateRoomAssignmentsCommand command) {
        RoomingList roomingList = LogisticsLookups.requireRoomingListById(roomingListRepository, command.roomingListId(), command.promoterId());
        roomingList.applyAssignments(LogisticsInputMapper.toRoomAssignments(command.assignments()));
        return roomingListRepository.save(roomingList);
    }
}

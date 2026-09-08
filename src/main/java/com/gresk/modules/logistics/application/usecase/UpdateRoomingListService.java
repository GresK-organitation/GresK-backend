package com.gresk.modules.logistics.application.usecase;

import com.gresk.modules.logistics.application.command.UpdateRoomingListCommand;
import com.gresk.modules.logistics.application.port.in.UpdateRoomingListUseCase;
import com.gresk.modules.logistics.domain.model.RoomingList;
import com.gresk.modules.logistics.domain.port.out.RoomingListRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class UpdateRoomingListService implements UpdateRoomingListUseCase {

    private final RoomingListRepositoryPort roomingListRepository;

    @Override
    public RoomingList execute(UpdateRoomingListCommand command) {
        RoomingList roomingList = LogisticsLookups.requireRoomingListById(roomingListRepository, command.roomingListId(), command.promoterId());
        roomingList.updateDetails(command.hotelName(), command.hotelAddress(), command.checkInDate(), command.checkOutDate());
        roomingList.updateAllotments(LogisticsInputMapper.toRoomAllotments(command.allotments()));
        return roomingListRepository.save(roomingList);
    }
}

package com.gresk.modules.logistics.application.usecase;

import com.gresk.modules.logistics.application.command.CreateRoomingListCommand;
import com.gresk.modules.logistics.application.port.in.CreateRoomingListUseCase;
import com.gresk.modules.logistics.domain.model.RoomingList;
import com.gresk.modules.logistics.domain.model.TourId;
import com.gresk.modules.logistics.domain.port.out.RoomingListRepositoryPort;
import com.gresk.modules.promoter.domain.model.valueobject.PromoterId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class CreateRoomingListService implements CreateRoomingListUseCase {

    private final RoomingListRepositoryPort roomingListRepository;

    @Override
    public RoomingList execute(CreateRoomingListCommand command) {
        RoomingList roomingList = RoomingList.create(TourId.of(command.tourId()), PromoterId.of(command.promoterId()),
                command.hotelName(), command.hotelAddress(), command.checkInDate(), command.checkOutDate(),
                LogisticsInputMapper.toRoomAllotments(command.allotments()));
        return roomingListRepository.save(roomingList);
    }
}

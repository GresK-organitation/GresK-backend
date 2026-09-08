package com.gresk.modules.logistics.infrastructure.persistence.adapter;

import com.gresk.modules.logistics.domain.model.RoomingList;
import com.gresk.modules.logistics.domain.model.RoomingListId;
import com.gresk.modules.logistics.domain.model.TourId;
import com.gresk.modules.logistics.domain.port.out.RoomingListRepositoryPort;
import com.gresk.modules.logistics.infrastructure.persistence.mapper.RoomingListMapper;
import com.gresk.modules.logistics.infrastructure.persistence.repository.RoomingListJpaRepository;
import com.gresk.modules.promoter.domain.model.valueobject.PromoterId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class JpaRoomingListRepositoryAdapter implements RoomingListRepositoryPort {

    private final RoomingListJpaRepository jpaRepository;
    private final RoomingListMapper mapper;

    @Override
    @Transactional
    public RoomingList save(RoomingList roomingList) {
        return mapper.toDomain(jpaRepository.save(mapper.toEntity(roomingList)));
    }

    @Override
    public Optional<RoomingList> findById(RoomingListId id) {
        return jpaRepository.findById(id.value()).map(mapper::toDomain);
    }

    @Override
    public Optional<RoomingList> findByIdAndPromoterId(RoomingListId id, PromoterId promoterId) {
        return jpaRepository.findByIdAndPromoterId(id.value(), promoterId.value()).map(mapper::toDomain);
    }

    @Override
    public List<RoomingList> findAllByTourId(TourId tourId) {
        return jpaRepository.findByTourId(tourId.value()).stream().map(mapper::toDomain).toList();
    }
}

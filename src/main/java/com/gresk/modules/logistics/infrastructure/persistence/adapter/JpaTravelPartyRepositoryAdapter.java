package com.gresk.modules.logistics.infrastructure.persistence.adapter;

import com.gresk.modules.logistics.domain.model.TourId;
import com.gresk.modules.logistics.domain.model.TravelParty;
import com.gresk.modules.logistics.domain.model.TravelPartyId;
import com.gresk.modules.logistics.domain.port.out.TravelPartyRepositoryPort;
import com.gresk.modules.logistics.infrastructure.persistence.mapper.TravelPartyMapper;
import com.gresk.modules.logistics.infrastructure.persistence.repository.TravelPartyJpaRepository;
import com.gresk.modules.promoter.domain.model.valueobject.PromoterId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class JpaTravelPartyRepositoryAdapter implements TravelPartyRepositoryPort {

    private final TravelPartyJpaRepository jpaRepository;
    private final TravelPartyMapper mapper;

    @Override
    @Transactional
    public TravelParty save(TravelParty travelParty) {
        return mapper.toDomain(jpaRepository.save(mapper.toEntity(travelParty)));
    }

    @Override
    public Optional<TravelParty> findById(TravelPartyId id) {
        return jpaRepository.findById(id.value()).map(mapper::toDomain);
    }

    @Override
    public Optional<TravelParty> findByIdAndPromoterId(TravelPartyId id, PromoterId promoterId) {
        return jpaRepository.findByIdAndPromoterId(id.value(), promoterId.value()).map(mapper::toDomain);
    }

    @Override
    public Optional<TravelParty> findByTourId(TourId tourId) {
        return jpaRepository.findByTourId(tourId.value()).map(mapper::toDomain);
    }
}

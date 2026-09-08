package com.gresk.modules.logistics.infrastructure.persistence.adapter;

import com.gresk.modules.logistics.domain.model.Itinerary;
import com.gresk.modules.logistics.domain.model.ItineraryId;
import com.gresk.modules.logistics.domain.model.TourId;
import com.gresk.modules.logistics.domain.port.out.ItineraryRepositoryPort;
import com.gresk.modules.logistics.infrastructure.persistence.mapper.ItineraryMapper;
import com.gresk.modules.logistics.infrastructure.persistence.repository.ItineraryJpaRepository;
import com.gresk.modules.promoter.domain.model.valueobject.PromoterId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class JpaItineraryRepositoryAdapter implements ItineraryRepositoryPort {

    private final ItineraryJpaRepository jpaRepository;
    private final ItineraryMapper mapper;

    @Override
    @Transactional
    public Itinerary save(Itinerary itinerary) {
        return mapper.toDomain(jpaRepository.save(mapper.toEntity(itinerary)));
    }

    @Override
    public Optional<Itinerary> findById(ItineraryId id) {
        return jpaRepository.findById(id.value()).map(mapper::toDomain);
    }

    @Override
    public Optional<Itinerary> findByIdAndPromoterId(ItineraryId id, PromoterId promoterId) {
        return jpaRepository.findByIdAndPromoterId(id.value(), promoterId.value()).map(mapper::toDomain);
    }

    @Override
    public Optional<Itinerary> findByTourId(TourId tourId) {
        return jpaRepository.findByTourId(tourId.value()).map(mapper::toDomain);
    }
}

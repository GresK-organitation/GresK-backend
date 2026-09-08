package com.gresk.modules.rider.infrastructure.persistence.adapter;

import com.gresk.modules.artist.domain.model.valueobject.ArtistId;
import com.gresk.modules.rider.domain.model.HospitalityRider;
import com.gresk.modules.rider.domain.model.RiderId;
import com.gresk.modules.rider.domain.port.out.HospitalityRiderRepositoryPort;
import com.gresk.modules.rider.infrastructure.persistence.mapper.HospitalityRiderMapper;
import com.gresk.modules.rider.infrastructure.persistence.repository.HospitalityRiderJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class JpaHospitalityRiderRepositoryAdapter implements HospitalityRiderRepositoryPort {

    private final HospitalityRiderJpaRepository repo;
    private final HospitalityRiderMapper        mapper;

    @Override
    @Transactional
    public HospitalityRider save(HospitalityRider rider) {
        return mapper.toDomain(repo.save(mapper.toEntity(rider)));
    }

    @Override
    public Optional<HospitalityRider> findById(RiderId id) {
        return repo.findById(id.value()).map(mapper::toDomain);
    }

    @Override
    public Optional<HospitalityRider> findByShareToken(String shareToken) {
        return repo.findByShareToken(shareToken).map(mapper::toDomain);
    }

    @Override
    public List<HospitalityRider> findByArtistId(ArtistId artistId) {
        return repo.findByArtistId(artistId.value()).stream()
                .map(mapper::toDomain).toList();
    }
}

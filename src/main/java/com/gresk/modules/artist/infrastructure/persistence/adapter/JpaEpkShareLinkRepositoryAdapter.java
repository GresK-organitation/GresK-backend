package com.gresk.modules.artist.infrastructure.persistence.adapter;

import com.gresk.modules.artist.domain.model.EpkShareLink;
import com.gresk.modules.artist.domain.model.valueobject.EpkAssetId;
import com.gresk.modules.artist.domain.model.valueobject.EpkShareLinkId;
import com.gresk.modules.artist.domain.port.out.EpkShareLinkRepositoryPort;
import com.gresk.modules.artist.infrastructure.persistence.mapper.EpkShareLinkMapper;
import com.gresk.modules.artist.infrastructure.persistence.repository.EpkShareLinkJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class JpaEpkShareLinkRepositoryAdapter implements EpkShareLinkRepositoryPort {

    private final EpkShareLinkJpaRepository jpaRepository;
    private final EpkShareLinkMapper        mapper;

    @Override
    @Transactional
    public EpkShareLink save(EpkShareLink link) {
        return mapper.toDomain(jpaRepository.save(mapper.toEntity(link)));
    }

    @Override
    public Optional<EpkShareLink> findById(EpkShareLinkId id) {
        return jpaRepository.findById(id.value()).map(mapper::toDomain);
    }

    @Override
    public Optional<EpkShareLink> findByToken(String token) {
        return jpaRepository.findByToken(token).map(mapper::toDomain);
    }

    @Override
    public List<EpkShareLink> findAllByEpkAssetId(EpkAssetId epkAssetId) {
        return jpaRepository.findByEpkAssetId(epkAssetId.value()).stream().map(mapper::toDomain).toList();
    }

    @Override
    @Transactional
    public void deleteExpiredBefore(Instant cutoff) {
        jpaRepository.deleteByExpiresAtBeforeAndRevokedFalse(cutoff);
    }
}

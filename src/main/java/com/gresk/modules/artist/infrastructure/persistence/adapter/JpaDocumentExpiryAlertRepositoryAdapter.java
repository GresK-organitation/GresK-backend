package com.gresk.modules.artist.infrastructure.persistence.adapter;

import com.gresk.modules.artist.domain.model.DocumentExpiryAlert;
import com.gresk.modules.artist.domain.model.valueobject.BandMemberId;
import com.gresk.modules.artist.domain.model.valueobject.DocumentExpiryAlertId;
import com.gresk.modules.artist.domain.model.valueobject.IdentityDocumentType;
import com.gresk.modules.artist.domain.port.out.DocumentExpiryAlertRepositoryPort;
import com.gresk.modules.artist.infrastructure.persistence.mapper.DocumentExpiryAlertMapper;
import com.gresk.modules.artist.infrastructure.persistence.repository.DocumentExpiryAlertJpaRepository;
import com.gresk.modules.promoter.domain.model.valueobject.PromoterId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class JpaDocumentExpiryAlertRepositoryAdapter implements DocumentExpiryAlertRepositoryPort {

    private final DocumentExpiryAlertJpaRepository jpaRepository;
    private final DocumentExpiryAlertMapper        mapper;

    @Override
    @Transactional
    public DocumentExpiryAlert save(DocumentExpiryAlert alert) {
        return mapper.toDomain(jpaRepository.save(mapper.toEntity(alert)));
    }

    @Override
    public Optional<DocumentExpiryAlert> findById(DocumentExpiryAlertId id) {
        return jpaRepository.findById(id.value()).map(mapper::toDomain);
    }

    @Override
    public List<DocumentExpiryAlert> findAllByPromoterId(PromoterId promoterId) {
        return jpaRepository.findByPromoterId(promoterId.value()).stream().map(mapper::toDomain).toList();
    }

    @Override
    public boolean existsUnreadFor(BandMemberId bandMemberId, IdentityDocumentType documentType) {
        return jpaRepository.existsByBandMemberIdAndDocumentTypeAndReadFalse(bandMemberId.value(), documentType);
    }
}

package com.gresk.modules.artist.infrastructure.persistence.repository;

import com.gresk.modules.artist.domain.model.valueobject.IdentityDocumentType;
import com.gresk.modules.artist.infrastructure.persistence.entity.DocumentExpiryAlertEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface DocumentExpiryAlertJpaRepository extends JpaRepository<DocumentExpiryAlertEntity, UUID> {
    List<DocumentExpiryAlertEntity> findByPromoterId(UUID promoterId);
    boolean existsByBandMemberIdAndDocumentTypeAndReadFalse(UUID bandMemberId, IdentityDocumentType documentType);
}

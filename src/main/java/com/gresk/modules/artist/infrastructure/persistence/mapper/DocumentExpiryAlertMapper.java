package com.gresk.modules.artist.infrastructure.persistence.mapper;

import com.gresk.modules.artist.domain.model.DocumentExpiryAlert;
import com.gresk.modules.artist.domain.model.valueobject.ArtistId;
import com.gresk.modules.artist.domain.model.valueobject.BandMemberId;
import com.gresk.modules.artist.domain.model.valueobject.DocumentExpiryAlertId;
import com.gresk.modules.artist.infrastructure.persistence.entity.DocumentExpiryAlertEntity;
import com.gresk.modules.promoter.domain.model.valueobject.PromoterId;
import org.springframework.stereotype.Component;

@Component
public class DocumentExpiryAlertMapper {

    public DocumentExpiryAlert toDomain(DocumentExpiryAlertEntity e) {
        return DocumentExpiryAlert.reconstitute(
                DocumentExpiryAlertId.of(e.getId()),
                PromoterId.of(e.getPromoterId()),
                ArtistId.of(e.getArtistId()),
                BandMemberId.of(e.getBandMemberId()),
                e.getDocumentType(),
                e.getExpiryDate(),
                e.getMessage(),
                e.isRead(),
                e.getCreatedAt()
        );
    }

    public DocumentExpiryAlertEntity toEntity(DocumentExpiryAlert a) {
        return DocumentExpiryAlertEntity.builder()
                .id(a.getId().value())
                .promoterId(a.getPromoterId().value())
                .artistId(a.getArtistId().value())
                .bandMemberId(a.getBandMemberId().value())
                .documentType(a.getDocumentType())
                .expiryDate(a.getExpiryDate())
                .message(a.getMessage())
                .read(a.isRead())
                .createdAt(a.getCreatedAt())
                .build();
    }
}

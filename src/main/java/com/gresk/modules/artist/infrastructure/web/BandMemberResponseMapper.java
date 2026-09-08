package com.gresk.modules.artist.infrastructure.web;

import com.gresk.modules.artist.domain.model.BandMember;
import com.gresk.modules.artist.domain.model.DocumentExpiryAlert;
import com.gresk.modules.artist.domain.model.valueobject.IdentityDocument;
import org.springframework.stereotype.Component;

@Component
public class BandMemberResponseMapper {

    public BandMemberResponse toResponse(BandMember m) {
        return new BandMemberResponse(
                m.getId().value().toString(),
                m.getArtistId().value().toString(),
                m.getName().value(),
                m.getRoleInBand(),
                m.isActive(),
                m.getDocuments().stream().map(this::toDocResponse).toList()
        );
    }

    private IdentityDocumentResponse toDocResponse(IdentityDocument d) {
        return new IdentityDocumentResponse(d.type().name(), d.documentNumber(), d.issuingCountry(), d.expiryDate());
    }

    public DocumentExpiryAlertResponse toAlertResponse(DocumentExpiryAlert a) {
        return new DocumentExpiryAlertResponse(
                a.getId().value().toString(),
                a.getPromoterId().value().toString(),
                a.getArtistId().value().toString(),
                a.getBandMemberId().value().toString(),
                a.getDocumentType().name(),
                a.getExpiryDate(),
                a.getMessage(),
                a.isRead(),
                a.getCreatedAt()
        );
    }
}

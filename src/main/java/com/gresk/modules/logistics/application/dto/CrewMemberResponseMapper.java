package com.gresk.modules.logistics.application.dto;

import com.gresk.modules.artist.domain.model.valueobject.IdentityDocument;
import com.gresk.modules.logistics.domain.model.CrewMember;
import org.springframework.stereotype.Component;

@Component
public class CrewMemberResponseMapper {

    public CrewMemberResponse toResponse(CrewMember crewMember) {
        return new CrewMemberResponse(
                crewMember.getId().toString(),
                crewMember.getPromoterId().toString(),
                crewMember.getName().value(),
                crewMember.getDefaultRole(),
                crewMember.getContactPhone(),
                crewMember.getContactEmail(),
                crewMember.getDocuments().stream().map(this::toDocumentView).toList(),
                crewMember.isActive(),
                crewMember.getCreatedAt(),
                crewMember.getUpdatedAt());
    }

    private CrewMemberResponse.DocumentView toDocumentView(IdentityDocument d) {
        return new CrewMemberResponse.DocumentView(d.type().name(), d.documentNumber(), d.issuingCountry(), d.expiryDate());
    }
}

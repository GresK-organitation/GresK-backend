package com.gresk.modules.logistics.infrastructure.persistence.mapper;

import com.gresk.modules.artist.domain.model.valueobject.IdentityDocument;
import com.gresk.modules.artist.infrastructure.persistence.entity.IdentityDocumentEmbeddable;
import com.gresk.modules.logistics.domain.model.CrewMember;
import com.gresk.modules.logistics.domain.model.CrewMemberId;
import com.gresk.modules.logistics.infrastructure.persistence.entity.CrewMemberEntity;
import com.gresk.modules.promoter.domain.model.valueobject.PromoterId;
import com.gresk.shared.domain.valueobject.Name;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class CrewMemberMapper {

    public CrewMember toDomain(CrewMemberEntity entity) {
        List<IdentityDocument> documents = entity.getDocuments().stream().map(this::toDomain).toList();
        return CrewMember.reconstitute(CrewMemberId.of(entity.getId()), PromoterId.of(entity.getPromoterId()),
                entity.getCreatedAt(), Name.reconstitute(entity.getName()), entity.getDefaultRole(),
                entity.getContactPhone(), entity.getContactEmail(), documents, entity.isActive(), entity.getUpdatedAt());
    }

    private IdentityDocument toDomain(IdentityDocumentEmbeddable e) {
        return IdentityDocument.of(e.getType(), e.getDocumentNumber(), e.getIssuingCountry(), e.getExpiryDate());
    }

    public CrewMemberEntity toEntity(CrewMember crewMember) {
        List<IdentityDocumentEmbeddable> documents = new ArrayList<>();
        for (IdentityDocument d : crewMember.getDocuments()) {
            documents.add(IdentityDocumentEmbeddable.builder().type(d.type()).documentNumber(d.documentNumber())
                    .issuingCountry(d.issuingCountry()).expiryDate(d.expiryDate()).build());
        }
        return CrewMemberEntity.builder()
                .id(crewMember.getId().value())
                .promoterId(crewMember.getPromoterId().value())
                .name(crewMember.getName().value())
                .defaultRole(crewMember.getDefaultRole())
                .contactPhone(crewMember.getContactPhone())
                .contactEmail(crewMember.getContactEmail())
                .active(crewMember.isActive())
                .createdAt(crewMember.getCreatedAt())
                .updatedAt(crewMember.getUpdatedAt())
                .documents(documents)
                .build();
    }
}

package com.gresk.modules.artist.infrastructure.persistence.mapper;

import com.gresk.modules.artist.domain.model.BandMember;
import com.gresk.modules.artist.domain.model.valueobject.ArtistId;
import com.gresk.modules.artist.domain.model.valueobject.BandMemberId;
import com.gresk.modules.artist.domain.model.valueobject.IdentityDocument;
import com.gresk.modules.artist.infrastructure.persistence.entity.BandMemberEntity;
import com.gresk.modules.artist.infrastructure.persistence.entity.IdentityDocumentEmbeddable;
import com.gresk.modules.promoter.domain.model.valueobject.PromoterId;
import com.gresk.shared.domain.valueobject.Name;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class BandMemberMapper {

    public BandMember toDomain(BandMemberEntity e) {
        List<IdentityDocument> docs = e.getDocuments().stream()
                .map(d -> new IdentityDocument(d.getType(), d.getDocumentNumber(),
                        d.getIssuingCountry(), d.getExpiryDate()))
                .toList();

        return BandMember.reconstitute(
                BandMemberId.of(e.getId()),
                ArtistId.of(e.getArtistId()),
                PromoterId.of(e.getPromoterId()),
                Name.reconstitute(e.getName()),
                e.getRoleInBand(),
                docs,
                e.isActive(),
                e.getCreatedAt(),
                e.getUpdatedAt()
        );
    }

    public BandMemberEntity toEntity(BandMember m) {
        return BandMemberEntity.builder()
                .id(m.getId().value())
                .artistId(m.getArtistId().value())
                .promoterId(m.getPromoterId().value())
                .name(m.getName().value())
                .roleInBand(m.getRoleInBand())
                .documents(toEmbeddableDocuments(m.getDocuments()))
                .active(m.isActive())
                .createdAt(m.getCreatedAt())
                .updatedAt(m.getUpdatedAt())
                .build();
    }

    public List<IdentityDocumentEmbeddable> toEmbeddableDocuments(List<IdentityDocument> docs) {
        List<IdentityDocumentEmbeddable> result = new ArrayList<>();
        for (IdentityDocument d : docs) {
            result.add(IdentityDocumentEmbeddable.builder()
                    .type(d.type())
                    .documentNumber(d.documentNumber())
                    .issuingCountry(d.issuingCountry())
                    .expiryDate(d.expiryDate())
                    .build());
        }
        return result;
    }
}

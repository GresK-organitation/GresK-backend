package com.gresk.modules.artist.infrastructure.persistence.mapper;

import com.gresk.modules.artist.domain.model.RosterMember;
import com.gresk.modules.artist.domain.model.valueobject.ArtistId;
import com.gresk.modules.artist.domain.model.valueobject.BillingDetails;
import com.gresk.modules.artist.domain.model.valueobject.RosterMemberId;
import com.gresk.modules.artist.infrastructure.persistence.entity.RosterMemberEntity;
import com.gresk.modules.promoter.domain.model.valueobject.PromoterId;
import com.gresk.shared.domain.valueobject.Email;
import com.gresk.shared.domain.valueobject.Name;
import org.springframework.stereotype.Component;

@Component
public class RosterMemberMapper {

    public RosterMember toDomain(RosterMemberEntity e) {
        return RosterMember.reconstitute(
                RosterMemberId.of(e.getId()),
                ArtistId.of(e.getArtistId()),
                PromoterId.of(e.getPromoterId()),
                Name.reconstitute(e.getName()),
                e.getRole(),
                e.getPhone(),
                e.getEmail() != null ? Email.reconstitute(e.getEmail()) : null,
                BillingDetails.reconstitute(e.getBillingLegalName(), e.getBillingTaxId(),
                        e.getBillingAddress(), e.getBillingIban()),
                e.isPrimaryContact(),
                e.isActive(),
                e.getCreatedAt(),
                e.getUpdatedAt()
        );
    }

    public RosterMemberEntity toEntity(RosterMember m) {
        BillingDetails billing = m.getBillingDetails();
        return RosterMemberEntity.builder()
                .id(m.getId().value())
                .artistId(m.getArtistId().value())
                .promoterId(m.getPromoterId().value())
                .name(m.getName().value())
                .role(m.getRole())
                .phone(m.getPhone())
                .email(m.getEmail() != null ? m.getEmail().value() : null)
                .billingLegalName(billing != null ? billing.legalName() : null)
                .billingTaxId(billing != null ? billing.taxId() : null)
                .billingAddress(billing != null ? billing.billingAddress() : null)
                .billingIban(billing != null ? billing.iban() : null)
                .primaryContact(m.isPrimary())
                .active(m.isActive())
                .createdAt(m.getCreatedAt())
                .updatedAt(m.getUpdatedAt())
                .build();
    }
}

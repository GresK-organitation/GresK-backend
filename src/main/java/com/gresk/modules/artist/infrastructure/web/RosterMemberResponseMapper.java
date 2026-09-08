package com.gresk.modules.artist.infrastructure.web;

import com.gresk.modules.artist.domain.model.RosterMember;
import com.gresk.modules.artist.domain.model.valueobject.BillingDetails;
import org.springframework.stereotype.Component;

@Component
public class RosterMemberResponseMapper {

    public RosterMemberResponse toResponse(RosterMember m) {
        BillingDetails billing = m.getBillingDetails();
        return new RosterMemberResponse(
                m.getId().value().toString(),
                m.getArtistId().value().toString(),
                m.getName().value(),
                m.getRole().name(),
                m.getPhone(),
                m.getEmail() != null ? m.getEmail().value() : null,
                billing != null ? billing.legalName()      : null,
                billing != null ? billing.taxId()          : null,
                billing != null ? billing.billingAddress() : null,
                billing != null ? billing.iban()           : null,
                m.isPrimary(),
                m.isActive()
        );
    }
}

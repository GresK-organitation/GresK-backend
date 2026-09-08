package com.gresk.modules.artist.application.usecase;

import com.gresk.modules.artist.application.command.UpdateRosterMemberCommand;
import com.gresk.modules.artist.application.port.in.UpdateRosterMemberPort;
import com.gresk.modules.artist.domain.exception.RosterMemberNotFoundException;
import com.gresk.modules.artist.domain.model.RosterMember;
import com.gresk.modules.artist.domain.model.valueobject.BillingDetails;
import com.gresk.modules.artist.domain.model.valueobject.ContactRole;
import com.gresk.modules.artist.domain.model.valueobject.RosterMemberId;
import com.gresk.modules.artist.domain.port.out.RosterMemberRepositoryPort;
import com.gresk.modules.promoter.domain.model.valueobject.PromoterId;
import com.gresk.shared.domain.valueobject.Email;
import com.gresk.shared.domain.valueobject.Name;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class UpdateRosterMemberUseCase implements UpdateRosterMemberPort {

    private final RosterMemberRepositoryPort rosterMemberRepository;

    @Override
    public RosterMember execute(UpdateRosterMemberCommand command) {
        PromoterId promoterId = PromoterId.of(command.promoterId());
        RosterMember member = rosterMemberRepository
                .findByIdAndPromoterId(RosterMemberId.of(command.rosterMemberId()), promoterId)
                .orElseThrow(() -> new RosterMemberNotFoundException(command.rosterMemberId()));

        member.updateContactInfo(
                Name.of(command.name()),
                ContactRole.valueOf(command.role()),
                command.phone(),
                command.email() != null && !command.email().isBlank() ? Email.of(command.email()) : null
        );
        member.updateBillingDetails(BillingDetails.of(command.billingLegalName(), command.billingTaxId(),
                command.billingAddress(), command.billingIban()));

        if (command.primary()) member.markAsPrimary(); else member.unmarkAsPrimary();
        if (command.active()) member.reactivate(); else member.deactivate();

        return rosterMemberRepository.save(member);
    }
}

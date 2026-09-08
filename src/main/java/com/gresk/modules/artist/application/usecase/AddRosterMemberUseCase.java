package com.gresk.modules.artist.application.usecase;

import com.gresk.modules.artist.application.command.AddRosterMemberCommand;
import com.gresk.modules.artist.application.port.in.AddRosterMemberPort;
import com.gresk.modules.artist.domain.model.RosterMember;
import com.gresk.modules.artist.domain.model.valueobject.ArtistId;
import com.gresk.modules.artist.domain.model.valueobject.BillingDetails;
import com.gresk.modules.artist.domain.model.valueobject.ContactRole;
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
public class AddRosterMemberUseCase implements AddRosterMemberPort {

    private final RosterMemberRepositoryPort rosterMemberRepository;

    @Override
    public RosterMember execute(AddRosterMemberCommand command) {
        RosterMember member = RosterMember.create(
                ArtistId.of(command.artistId()),
                PromoterId.of(command.promoterId()),
                Name.of(command.name()),
                ContactRole.valueOf(command.role()),
                command.phone(),
                command.email() != null && !command.email().isBlank() ? Email.of(command.email()) : null,
                BillingDetails.of(command.billingLegalName(), command.billingTaxId(),
                        command.billingAddress(), command.billingIban()),
                command.primary()
        );
        return rosterMemberRepository.save(member);
    }
}

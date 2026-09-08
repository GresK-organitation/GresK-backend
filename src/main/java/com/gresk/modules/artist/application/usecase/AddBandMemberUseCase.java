package com.gresk.modules.artist.application.usecase;

import com.gresk.modules.artist.application.command.AddBandMemberCommand;
import com.gresk.modules.artist.application.port.in.AddBandMemberPort;
import com.gresk.modules.artist.domain.model.BandMember;
import com.gresk.modules.artist.domain.model.valueobject.ArtistId;
import com.gresk.modules.artist.domain.port.out.BandMemberRepositoryPort;
import com.gresk.modules.promoter.domain.model.valueobject.PromoterId;
import com.gresk.shared.domain.valueobject.Name;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class AddBandMemberUseCase implements AddBandMemberPort {

    private final BandMemberRepositoryPort bandMemberRepository;

    @Override
    public BandMember execute(AddBandMemberCommand command) {
        BandMember member = BandMember.create(
                ArtistId.of(command.artistId()),
                PromoterId.of(command.promoterId()),
                Name.of(command.name()),
                command.roleInBand()
        );
        return bandMemberRepository.save(member);
    }
}

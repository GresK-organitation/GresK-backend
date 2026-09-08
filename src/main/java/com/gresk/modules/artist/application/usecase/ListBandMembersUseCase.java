package com.gresk.modules.artist.application.usecase;

import com.gresk.modules.artist.application.port.in.ListBandMembersPort;
import com.gresk.modules.artist.domain.model.BandMember;
import com.gresk.modules.artist.domain.model.valueobject.ArtistId;
import com.gresk.modules.artist.domain.port.out.BandMemberRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ListBandMembersUseCase implements ListBandMembersPort {

    private final BandMemberRepositoryPort bandMemberRepository;

    @Override
    public List<BandMember> execute(String artistId, String promoterId) {
        return bandMemberRepository.findAllByArtistId(ArtistId.of(artistId));
    }
}

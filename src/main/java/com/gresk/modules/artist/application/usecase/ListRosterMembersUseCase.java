package com.gresk.modules.artist.application.usecase;

import com.gresk.modules.artist.application.port.in.ListRosterMembersPort;
import com.gresk.modules.artist.domain.model.RosterMember;
import com.gresk.modules.artist.domain.model.valueobject.ArtistId;
import com.gresk.modules.artist.domain.port.out.RosterMemberRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ListRosterMembersUseCase implements ListRosterMembersPort {

    private final RosterMemberRepositoryPort rosterMemberRepository;

    @Override
    public List<RosterMember> execute(String artistId, String promoterId) {
        return rosterMemberRepository.findAllByArtistId(ArtistId.of(artistId));
    }
}

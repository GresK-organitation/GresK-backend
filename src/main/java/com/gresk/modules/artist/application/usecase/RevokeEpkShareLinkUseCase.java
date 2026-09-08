package com.gresk.modules.artist.application.usecase;

import com.gresk.modules.artist.application.port.in.RevokeEpkShareLinkPort;
import com.gresk.modules.artist.domain.exception.EpkShareLinkNotFoundException;
import com.gresk.modules.artist.domain.exception.EpkShareLinkNotOwnedException;
import com.gresk.modules.artist.domain.model.EpkShareLink;
import com.gresk.modules.artist.domain.model.valueobject.EpkShareLinkId;
import com.gresk.modules.artist.domain.port.out.EpkShareLinkRepositoryPort;
import com.gresk.modules.promoter.domain.model.valueobject.PromoterId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class RevokeEpkShareLinkUseCase implements RevokeEpkShareLinkPort {

    private final EpkShareLinkRepositoryPort shareLinkRepository;

    @Override
    public void execute(String shareLinkId, String promoterId) {
        EpkShareLink link = shareLinkRepository.findById(EpkShareLinkId.of(shareLinkId))
                .orElseThrow(() -> new EpkShareLinkNotFoundException(shareLinkId));
        if (!link.getPromoterId().equals(PromoterId.of(promoterId))) {
            throw new EpkShareLinkNotOwnedException();
        }
        link.revoke();
        shareLinkRepository.save(link);
    }
}

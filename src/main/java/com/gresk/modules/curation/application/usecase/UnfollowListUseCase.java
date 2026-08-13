package com.gresk.modules.curation.application.usecase;

import com.gresk.modules.curation.domain.model.CuratedListId;
import com.gresk.modules.curation.domain.port.out.ListFollowerRepository;
import com.gresk.modules.user.domain.model.UserId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UnfollowListUseCase {

    private final ListFollowerRepository followerRepository;

    @Transactional
    public void execute(String listId, String userId) {
        followerRepository.unfollow(CuratedListId.of(listId), UserId.from(userId));
    }
}

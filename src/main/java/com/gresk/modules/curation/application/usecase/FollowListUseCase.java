package com.gresk.modules.curation.application.usecase;

import com.gresk.modules.curation.domain.model.CuratedListId;
import com.gresk.modules.curation.domain.port.out.ListFollowerRepository;
import com.gresk.modules.user.domain.model.UserId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class FollowListUseCase {

    private final ListFollowerRepository followerRepository;
    private final CuratedListAccessGuard accessGuard;

    @Transactional
    public void execute(String listId, String userId) {
        CuratedListId id = CuratedListId.of(listId);
        UserId follower = UserId.from(userId);
        accessGuard.requireVisible(id, follower); // can't follow a private list you don't own
        followerRepository.follow(id, follower);
    }
}

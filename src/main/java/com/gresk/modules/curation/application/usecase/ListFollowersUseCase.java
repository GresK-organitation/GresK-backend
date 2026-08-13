package com.gresk.modules.curation.application.usecase;

import com.gresk.modules.curation.domain.model.CuratedListId;
import com.gresk.modules.curation.domain.port.out.ListFollowerRepository;
import com.gresk.modules.user.domain.model.UserId;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ListFollowersUseCase {

    private final ListFollowerRepository followerRepository;
    private final CuratedListAccessGuard accessGuard;

    @Transactional(readOnly = true)
    public List<UserId> execute(String listId, String requesterUserId, int page, int size) {
        CuratedListId id = CuratedListId.of(listId);
        accessGuard.requireVisible(id, UserId.from(requesterUserId));
        return followerRepository.findFollowerUserIds(id, PageRequest.of(page, size));
    }

    @Transactional(readOnly = true)
    public long count(String listId, String requesterUserId) {
        CuratedListId id = CuratedListId.of(listId);
        accessGuard.requireVisible(id, UserId.from(requesterUserId));
        return followerRepository.countFollowers(id);
    }
}

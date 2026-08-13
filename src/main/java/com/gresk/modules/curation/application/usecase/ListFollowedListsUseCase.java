package com.gresk.modules.curation.application.usecase;

import com.gresk.modules.curation.domain.model.CuratedList;
import com.gresk.modules.curation.domain.model.CuratedListId;
import com.gresk.modules.curation.domain.port.out.CuratedListRepository;
import com.gresk.modules.curation.domain.port.out.ListFollowerRepository;
import com.gresk.modules.user.domain.model.UserId;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * list_followers.list_id has ON DELETE CASCADE on curated_lists, so every
 * followed id here is guaranteed to still resolve to an existing list.
 */
@Service
@RequiredArgsConstructor
public class ListFollowedListsUseCase {

    private final ListFollowerRepository followerRepository;
    private final CuratedListRepository  listRepository;

    @Transactional(readOnly = true)
    public List<CuratedList> execute(String userId, int page, int size) {
        List<CuratedListId> ids = followerRepository.findFollowedListIds(UserId.from(userId), PageRequest.of(page, size));
        return ids.stream()
                .map(id -> listRepository.findById(id).orElseThrow())
                .toList();
    }

    @Transactional(readOnly = true)
    public long count(String userId) {
        return followerRepository.countFollowed(UserId.from(userId));
    }
}

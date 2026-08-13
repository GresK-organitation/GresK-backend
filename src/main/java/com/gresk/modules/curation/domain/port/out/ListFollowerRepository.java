package com.gresk.modules.curation.domain.port.out;

import com.gresk.modules.curation.domain.model.CuratedListId;
import com.gresk.modules.user.domain.model.UserId;
import org.springframework.data.domain.PageRequest;

import java.util.List;

public interface ListFollowerRepository {
    void follow(CuratedListId listId, UserId userId);
    void unfollow(CuratedListId listId, UserId userId);
    boolean isFollowing(CuratedListId listId, UserId userId);
    long countFollowers(CuratedListId listId);

    List<UserId> findFollowerUserIds(CuratedListId listId, PageRequest pageRequest);

    List<CuratedListId> findFollowedListIds(UserId userId, PageRequest pageRequest);
    long countFollowed(UserId userId);
}

package com.gresk.modules.curation.infrastructure.persistence.adapter;

import com.gresk.modules.curation.domain.model.CuratedListId;
import com.gresk.modules.curation.domain.port.out.ListFollowerRepository;
import com.gresk.modules.curation.infrastructure.persistence.entity.CuratedListEntity;
import com.gresk.modules.curation.infrastructure.persistence.entity.ListFollowerEntity;
import com.gresk.modules.curation.infrastructure.persistence.repository.CuratedListJpaRepository;
import com.gresk.modules.curation.infrastructure.persistence.repository.ListFollowerJpaRepository;
import com.gresk.modules.user.domain.model.UserId;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Repository
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class JpaListFollowerRepositoryAdapter implements ListFollowerRepository {

    private final ListFollowerJpaRepository followerRepo;
    private final CuratedListJpaRepository  listRepo;

    @Override
    @Transactional
    public void follow(CuratedListId listId, UserId userId) {
        if (followerRepo.existsByList_IdAndUserId(listId.value(), userId.value())) {
            return; // idempotent
        }
        CuratedListEntity listRef = listRepo.getReferenceById(listId.value());
        followerRepo.save(ListFollowerEntity.builder()
                .list(listRef)
                .userId(userId.value())
                .followedAt(Instant.now())
                .build());
    }

    @Override
    @Transactional
    public void unfollow(CuratedListId listId, UserId userId) {
        followerRepo.deleteByList_IdAndUserId(listId.value(), userId.value()); // idempotent
    }

    @Override
    public boolean isFollowing(CuratedListId listId, UserId userId) {
        return followerRepo.existsByList_IdAndUserId(listId.value(), userId.value());
    }

    @Override
    public long countFollowers(CuratedListId listId) {
        return followerRepo.countByList_Id(listId.value());
    }

    @Override
    public List<UserId> findFollowerUserIds(CuratedListId listId, PageRequest pageRequest) {
        return followerRepo.findByList_Id(listId.value(), pageRequest).stream()
                .map(f -> UserId.of(f.getUserId()))
                .toList();
    }

    @Override
    public List<CuratedListId> findFollowedListIds(UserId userId, PageRequest pageRequest) {
        return followerRepo.findByUserId(userId.value(), pageRequest).stream()
                .map(f -> CuratedListId.of(f.getList().getId()))
                .toList();
    }

    @Override
    public long countFollowed(UserId userId) {
        return followerRepo.countByUserId(userId.value());
    }
}

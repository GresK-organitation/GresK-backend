package com.gresk.modules.review.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SpringDataReviewRepository extends JpaRepository<ReviewEntity, UUID> {
    List<ReviewEntity>     findByUserId(UUID userId);
    List<ReviewEntity>     findByEventId(UUID eventId);
    Optional<ReviewEntity> findByTicketId(UUID ticketId);
    boolean                existsByTicketId(UUID ticketId);

    // ── Batch like queries ────────────────────────────────────────────────────

    @Query("SELECT r.id AS reviewId, COUNT(l) AS likeCount " +
           "FROM ReviewEntity r LEFT JOIN r.likedBy l " +
           "WHERE r.id IN :ids " +
           "GROUP BY r.id")
    List<LikeCountProjection> countLikesByReviewIds(@Param("ids") List<UUID> ids);

    @Query("SELECT r.id FROM ReviewEntity r JOIN r.likedBy l " +
           "WHERE r.id IN :ids AND l = :userId")
    List<UUID> findReviewIdsLikedByUser(@Param("ids") List<UUID> ids,
                                        @Param("userId") UUID userId);

    interface LikeCountProjection {
        UUID getReviewId();
        Long getLikeCount();
    }
}

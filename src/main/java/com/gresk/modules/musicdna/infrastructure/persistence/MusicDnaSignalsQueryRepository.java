package com.gresk.modules.musicdna.infrastructure.persistence;

import com.gresk.modules.user.infrastructure.persistence.entity.UserEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * Repositorio de solo lectura para las señales agregadas del ADN Musical.
 * Vive en `musicdna` (no en `review`/`journal`/`user`) para evitar
 * dependencias inversas, mismo criterio que TendenciasArtistQueryRepository.
 * Cada query está indexada por user_id — O(1) queries por usuario, no N+1.
 */
public interface MusicDnaSignalsQueryRepository extends Repository<UserEntity, UUID> {

    @Query(value = """
        SELECT
          COUNT(*)                                                             AS reviewCount,
          COUNT(*) FILTER (WHERE r.comment IS NOT NULL AND r.comment <> '')    AS writtenCount,
          COALESCE(SUM(rl.like_count), 0)                                     AS totalLikes,
          COUNT(*) FILTER (WHERE e.city = u.city)                             AS localMatches
        FROM reviews r
        JOIN events e ON e.id = r.event_id
        JOIN users  u ON u.id = r.user_id
        LEFT JOIN (SELECT review_id, COUNT(*) AS like_count FROM review_likes GROUP BY review_id) rl
               ON rl.review_id = r.id
        WHERE r.user_id = :userId
        """, nativeQuery = true)
    ReviewSignalsRow findReviewSignals(@Param("userId") UUID userId);

    @Query(value = """
        SELECT
          COUNT(*)                                                          AS journalCount,
          COUNT(*) FILTER (WHERE je.notes IS NOT NULL AND je.notes <> '')   AS writtenCount,
          COALESCE(SUM(jsonb_array_length(je.rating_criteria)), 0)          AS totalCustomCriteria,
          COUNT(*) FILTER (WHERE je.city = u.city)                         AS localMatches
        FROM journal_entries je
        JOIN users u ON u.id = je.user_id
        WHERE je.user_id = :userId
        """, nativeQuery = true)
    JournalSignalsRow findJournalSignals(@Param("userId") UUID userId);

    @Query(value = """
        SELECT COUNT(*) FROM (
          SELECT genre FROM (
            SELECT e.genre AS genre FROM reviews r JOIN events e ON e.id = r.event_id
              WHERE r.user_id = :userId AND e.genre IS NOT NULL
            UNION ALL
            SELECT je.genre AS genre FROM journal_entries je
              WHERE je.user_id = :userId AND je.genre IS NOT NULL
          ) combined
          GROUP BY genre
          HAVING COUNT(*) >= 2
        ) genres_with_min2
        """, nativeQuery = true)
    Integer countDistinctGenresMin2(@Param("userId") UUID userId);

    @Query(value = """
        SELECT MIN(oldest_date) FROM (
          SELECT e.event_date::date AS oldest_date
          FROM reviews r JOIN events e ON e.id = r.event_id
          WHERE r.user_id = :userId AND e.event_date IS NOT NULL
          UNION ALL
          SELECT CASE WHEN je.source = 'BULK_IMPORT'
                      THEN GREATEST(je.approx_date, (CURRENT_DATE - INTERVAL '5 years')::date)
                      ELSE je.approx_date END AS oldest_date
          FROM journal_entries je
          WHERE je.user_id = :userId
        ) combined
        """, nativeQuery = true)
    LocalDate findOldestDocumentedDate(@Param("userId") UUID userId);

    @Query(value = """
        SELECT user_id FROM reviews WHERE created_at >= :since
        UNION
        SELECT user_id FROM journal_entries WHERE created_at >= :since
        """, nativeQuery = true)
    List<UUID> findUserIdsWithActivitySince(@Param("since") Instant since);
}

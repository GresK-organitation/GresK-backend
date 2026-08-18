package com.gresk.modules.tendencias.stats.infrastructure.persistence;

import com.gresk.modules.review.infrastructure.persistence.ReviewEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * Repositorio de solo lectura para las tendencias de artistas. Vive en
 * `tendencias` (no en `artist`/`review`) para evitar dependencias inversas,
 * mismo criterio que PromoterStatsQueryRepository.
 */
public interface TendenciasArtistQueryRepository extends Repository<ReviewEntity, UUID> {

    @Query(value = """
        SELECT e.artist_id AS artistId, a.name AS artistName, COUNT(r.id) AS reviewCount
        FROM reviews r
        JOIN events e ON e.id = r.event_id
        JOIN artists a ON a.id = e.artist_id
        WHERE e.artist_id IS NOT NULL
          AND r.created_at BETWEEN :from AND :to
        GROUP BY e.artist_id, a.name
        ORDER BY reviewCount DESC
        LIMIT :limit
        """, nativeQuery = true)
    List<MostReviewedArtistRow> findMostReviewedArtists(
            @Param("from") Instant from, @Param("to") Instant to, @Param("limit") int limit);

    @Query(value = """
        SELECT e.artist_id AS artistId, a.name AS artistName,
               AVG(r.overall_rating) AS avgRating, COUNT(r.id) AS reviewCount
        FROM reviews r
        JOIN events e ON e.id = r.event_id
        JOIN artists a ON a.id = e.artist_id
        WHERE e.artist_id IS NOT NULL
        GROUP BY e.artist_id, a.name
        HAVING COUNT(r.id) >= :minReviews
        ORDER BY avgRating DESC
        LIMIT :limit
        """, nativeQuery = true)
    List<TopRatedArtistRow> findTopRatedArtists(
            @Param("minReviews") int minReviews, @Param("limit") int limit);
}

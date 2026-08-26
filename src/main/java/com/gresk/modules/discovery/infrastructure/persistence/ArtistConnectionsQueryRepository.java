package com.gresk.modules.discovery.infrastructure.persistence;

import com.gresk.modules.artist.infrastructure.persistence.ArtistEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

/**
 * Modo "Conexiones": artistas relacionados por co-reseña — usuarios que
 * reseñaron el artista dado también reseñaron estos otros, ordenados por
 * frecuencia de co-ocurrencia.
 */
public interface ArtistConnectionsQueryRepository extends Repository<ArtistEntity, UUID> {

    @Query(value = """
        SELECT
          a2.id::text          AS artistId,
          a2.name               AS name,
          a2.image_asset_id     AS imageAssetId,
          COUNT(*)               AS coReviewCount
        FROM reviews r1
        JOIN events e1 ON e1.id = r1.event_id AND e1.artist_id = :artistId
        JOIN reviews r2 ON r2.user_id = r1.user_id AND r2.id <> r1.id
        JOIN events e2 ON e2.id = r2.event_id AND e2.artist_id IS NOT NULL AND e2.artist_id <> :artistId
        JOIN artists a2 ON a2.id = e2.artist_id
        GROUP BY a2.id, a2.name, a2.image_asset_id
        ORDER BY coReviewCount DESC
        LIMIT :limit
        """, nativeQuery = true)
    List<ArtistConnectionRow> findConnections(@Param("artistId") UUID artistId, @Param("limit") int limit);
}

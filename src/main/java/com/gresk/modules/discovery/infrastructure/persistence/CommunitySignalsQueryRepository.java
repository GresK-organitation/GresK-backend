package com.gresk.modules.discovery.infrastructure.persistence;

import com.gresk.modules.artist.infrastructure.persistence.ArtistEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

/**
 * Agrega señales de comunidad GresK (reseñas, asistentes verificados,
 * usuarios que conocen al artista) desde `reviews`, `journal_entries` y
 * `artist_demand_signals`. Vive en `discovery` para evitar dependencias
 * inversas — mismo criterio que MusicDnaSignalsQueryRepository.
 */
public interface CommunitySignalsQueryRepository extends Repository<ArtistEntity, UUID> {

    @Query(value = """
        SELECT
          COUNT(*)                    AS reviewCount,
          COUNT(DISTINCT r.user_id)   AS verifiedAttendeesCount
        FROM reviews r
        JOIN events e ON e.id = r.event_id
        WHERE e.artist_id = :artistId
        """, nativeQuery = true)
    CommunitySignalsRow findReviewSignals(@Param("artistId") UUID artistId);

    @Query(value = """
        SELECT COUNT(*) FROM (
          SELECT r.user_id FROM reviews r JOIN events e ON e.id = r.event_id WHERE e.artist_id = :artistId
          UNION
          SELECT je.user_id FROM journal_entries je WHERE je.artist_id = :artistId
          UNION
          SELECT ds.user_id FROM artist_demand_signals ds WHERE ds.artist_id = :artistId
        ) combined
        """, nativeQuery = true)
    long countKnownBy(@Param("artistId") UUID artistId);
}

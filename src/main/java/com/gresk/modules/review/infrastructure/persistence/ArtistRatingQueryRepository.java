package com.gresk.modules.review.infrastructure.persistence;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

/**
 * Repositorio de solo lectura para calcular la nota media de los artistas
 * a partir de las valoraciones (artist_rating) de sus eventos.
 */
public interface ArtistRatingQueryRepository extends Repository<ReviewEntity, UUID> {

    /**
     * Media de artist_rating de todas las reviews de eventos vinculados
     * al artist_id indicado. Devuelve 0.0 si no hay reviews.
     */
    @Query(value = """
            SELECT COALESCE(AVG(r.artist_rating), 0.0)
            FROM reviews r
            JOIN events e ON r.event_id = e.id
            WHERE e.artist_id = :artistId
            """, nativeQuery = true)
    Double getAvgArtistRatingByArtistId(@Param("artistId") UUID artistId);
}

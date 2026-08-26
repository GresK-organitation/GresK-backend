package com.gresk.modules.discovery.infrastructure.persistence;

import com.gresk.modules.artist.infrastructure.persistence.ArtistEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

/**
 * Consulta de solo lectura que combina `artists` (módulo `artist`) con
 * `artist_discovery_profile` (módulo `discovery`) para los filtros de
 * búsqueda de Discovery. Vive en `discovery` para evitar dependencias
 * inversas — mismo criterio que MusicDnaSignalsQueryRepository. LEFT JOIN
 * porque un artista puede no tener perfil calculado todavía (job semanal
 * no ha corrido aún): en ese caso se trata como NO_SPOTIFY / score 0.
 */
public interface DiscoveryArtistQueryRepository extends Repository<ArtistEntity, UUID> {

    String SELECT_CLAUSE = """
        SELECT
          a.id::text                                                       AS artistId,
          a.name                                                           AS name,
          a.origin                                                         AS origin,
          (SELECT string_agg(ag.genre, ',') FROM artist_genres ag WHERE ag.artist_id = a.id) AS genres,
          a.image_asset_id                                                 AS imageAssetId,
          COALESCE(dp.size_tier, 'NO_SPOTIFY')                             AS sizeTier,
          dp.spotify_popularity                                            AS spotifyPopularity,
          COALESCE(dp.gresk_score, 0)                                      AS greskScore,
          a.avg_rating                                                     AS avgRating,
          COALESCE(dp.gresk_review_count, 0)                               AS greskReviewCount,
          COALESCE(dp.gresk_demand_count, 0)                               AS greskDemandCount,
          COALESCE(dp.has_upcoming_events, false)                          AS hasUpcomingEvents,
          dp.next_event_date                                               AS nextEventDate,
          dp.next_event_city                                               AS nextEventCity
        FROM artists a
        LEFT JOIN artist_discovery_profile dp ON dp.artist_id = a.id
        """;

    String WHERE_CLAUSE = """
        WHERE (:sizeTiersCsv = '' OR COALESCE(dp.size_tier, 'NO_SPOTIFY') = ANY(string_to_array(:sizeTiersCsv, ',')))
          AND (:city IS NULL OR LOWER(a.origin) LIKE LOWER(CONCAT('%', :city, '%')) OR LOWER(COALESCE(dp.mb_city, '')) LIKE LOWER(CONCAT('%', :city, '%')))
          AND (:country IS NULL OR LOWER(COALESCE(dp.mb_country, '')) LIKE LOWER(CONCAT('%', :country, '%')))
          AND (:genresCsv = '' OR EXISTS (SELECT 1 FROM artist_genres ag2 WHERE ag2.artist_id = a.id AND ag2.genre = ANY(string_to_array(:genresCsv, ','))))
          AND (:excludeGenresCsv = '' OR NOT EXISTS (SELECT 1 FROM artist_genres ag3 WHERE ag3.artist_id = a.id AND ag3.genre = ANY(string_to_array(:excludeGenresCsv, ','))))
          AND (:onlyWithReviews = false OR COALESCE(dp.gresk_review_count, 0) > 0)
          AND (:onlyWithDemandInMyCity = false OR EXISTS (SELECT 1 FROM artist_demand_signals ds WHERE ds.artist_id = a.id AND LOWER(ds.city) = LOWER(:demandCity)))
          AND (:onlyNewOnPlatform = false OR a.created_at >= NOW() - INTERVAL '30 days')
          AND (:onlyWithMusicBrainz = false OR dp.musicbrainz_id IS NOT NULL)
          AND (
            :liveFilter IS NULL
            OR (:liveFilter = 'THIS_WEEK' AND dp.has_upcoming_events = true AND dp.next_event_date BETWEEN CURRENT_DATE AND CURRENT_DATE + INTERVAL '7 days')
            OR (:liveFilter = 'THIS_MONTH' AND dp.has_upcoming_events = true AND dp.next_event_date BETWEEN CURRENT_DATE AND CURRENT_DATE + INTERVAL '30 days')
            OR (:liveFilter = 'IN_MY_CITY' AND dp.has_upcoming_events = true AND LOWER(dp.next_event_city) = LOWER(:liveCity))
            OR (:liveFilter = 'EVER' AND EXISTS (SELECT 1 FROM events e WHERE e.artist_id = a.id))
          )
        """;

    @Query(value = SELECT_CLAUSE + WHERE_CLAUSE + " ORDER BY greskScore DESC LIMIT :limit OFFSET :offset",
            nativeQuery = true)
    List<DiscoveryArtistRow> searchByGreskScore(
            @Param("sizeTiersCsv") String sizeTiersCsv, @Param("city") String city, @Param("country") String country,
            @Param("genresCsv") String genresCsv, @Param("excludeGenresCsv") String excludeGenresCsv,
            @Param("onlyWithReviews") boolean onlyWithReviews,
            @Param("onlyWithDemandInMyCity") boolean onlyWithDemandInMyCity, @Param("demandCity") String demandCity,
            @Param("onlyNewOnPlatform") boolean onlyNewOnPlatform, @Param("onlyWithMusicBrainz") boolean onlyWithMusicBrainz,
            @Param("liveFilter") String liveFilter, @Param("liveCity") String liveCity,
            @Param("limit") int limit, @Param("offset") long offset);

    @Query(value = SELECT_CLAUSE + WHERE_CLAUSE
            + " ORDER BY (dp.spotify_popularity IS NULL) DESC, dp.spotify_popularity ASC LIMIT :limit OFFSET :offset",
            nativeQuery = true)
    List<DiscoveryArtistRow> searchBySmallest(
            @Param("sizeTiersCsv") String sizeTiersCsv, @Param("city") String city, @Param("country") String country,
            @Param("genresCsv") String genresCsv, @Param("excludeGenresCsv") String excludeGenresCsv,
            @Param("onlyWithReviews") boolean onlyWithReviews,
            @Param("onlyWithDemandInMyCity") boolean onlyWithDemandInMyCity, @Param("demandCity") String demandCity,
            @Param("onlyNewOnPlatform") boolean onlyNewOnPlatform, @Param("onlyWithMusicBrainz") boolean onlyWithMusicBrainz,
            @Param("liveFilter") String liveFilter, @Param("liveCity") String liveCity,
            @Param("limit") int limit, @Param("offset") long offset);

    @Query(value = SELECT_CLAUSE + WHERE_CLAUSE + " ORDER BY a.created_at DESC LIMIT :limit OFFSET :offset",
            nativeQuery = true)
    List<DiscoveryArtistRow> searchByMostRecent(
            @Param("sizeTiersCsv") String sizeTiersCsv, @Param("city") String city, @Param("country") String country,
            @Param("genresCsv") String genresCsv, @Param("excludeGenresCsv") String excludeGenresCsv,
            @Param("onlyWithReviews") boolean onlyWithReviews,
            @Param("onlyWithDemandInMyCity") boolean onlyWithDemandInMyCity, @Param("demandCity") String demandCity,
            @Param("onlyNewOnPlatform") boolean onlyNewOnPlatform, @Param("onlyWithMusicBrainz") boolean onlyWithMusicBrainz,
            @Param("liveFilter") String liveFilter, @Param("liveCity") String liveCity,
            @Param("limit") int limit, @Param("offset") long offset);

    @Query(value = SELECT_CLAUSE + WHERE_CLAUSE + " ORDER BY a.created_at ASC LIMIT :limit OFFSET :offset",
            nativeQuery = true)
    List<DiscoveryArtistRow> searchByEarliestDiscovery(
            @Param("sizeTiersCsv") String sizeTiersCsv, @Param("city") String city, @Param("country") String country,
            @Param("genresCsv") String genresCsv, @Param("excludeGenresCsv") String excludeGenresCsv,
            @Param("onlyWithReviews") boolean onlyWithReviews,
            @Param("onlyWithDemandInMyCity") boolean onlyWithDemandInMyCity, @Param("demandCity") String demandCity,
            @Param("onlyNewOnPlatform") boolean onlyNewOnPlatform, @Param("onlyWithMusicBrainz") boolean onlyWithMusicBrainz,
            @Param("liveFilter") String liveFilter, @Param("liveCity") String liveCity,
            @Param("limit") int limit, @Param("offset") long offset);

    @Query(value = "SELECT COUNT(*) FROM artists a LEFT JOIN artist_discovery_profile dp ON dp.artist_id = a.id "
            + WHERE_CLAUSE, nativeQuery = true)
    long count(
            @Param("sizeTiersCsv") String sizeTiersCsv, @Param("city") String city, @Param("country") String country,
            @Param("genresCsv") String genresCsv, @Param("excludeGenresCsv") String excludeGenresCsv,
            @Param("onlyWithReviews") boolean onlyWithReviews,
            @Param("onlyWithDemandInMyCity") boolean onlyWithDemandInMyCity, @Param("demandCity") String demandCity,
            @Param("onlyNewOnPlatform") boolean onlyNewOnPlatform, @Param("onlyWithMusicBrainz") boolean onlyWithMusicBrainz,
            @Param("liveFilter") String liveFilter, @Param("liveCity") String liveCity);
}

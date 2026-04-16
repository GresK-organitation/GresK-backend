package com.gresk.modules.user.infrastructure.persistence;

import com.gresk.modules.event.domain.model.EventStatus;
import com.gresk.modules.event.infrastructure.persistence.EventEntity;
import com.gresk.shared.domain.MusicGenre;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

/**
 * Repositorio de solo lectura para recomendaciones de eventos al usuario.
 * Vive en el módulo User para evitar dependencias inversas hacia el módulo Event.
 * Reutiliza EventEntity directamente sobre la tabla events.
 * Patrón idéntico a PromoterEventQueryRepository.
 */
public interface UserEventQueryRepository extends Repository<EventEntity, UUID> {

    /**
     * Eventos recomendados filtrados por ciudad del usuario Y sus géneros preferidos.
     * Usa Pageable para limitar el resultado (PageRequest.of(0, 3)).
     */
    @Query("""
            SELECT e FROM EventEntity e
            WHERE e.status = :status
              AND e.eventDate >= :now
              AND e.city = :city
              AND e.genre IN :genres
            ORDER BY e.eventDate ASC
            """)
    List<EventEntity> findRecommendedEvents(
            @Param("city")   String                 city,
            @Param("genres") Collection<MusicGenre> genres,
            @Param("now")    Instant                now,
            @Param("status") EventStatus            status,
            Pageable                                pageable
    );

    /**
     * Fallback: mismos géneros, sin filtro de ciudad.
     * Se usa cuando no hay eventos en la ciudad del usuario.
     */
    @Query("""
            SELECT e FROM EventEntity e
            WHERE e.status = :status
              AND e.eventDate >= :now
              AND e.genre IN :genres
            ORDER BY e.eventDate ASC
            """)
    List<EventEntity> findRecommendedEventsByGenres(
            @Param("genres") Collection<MusicGenre> genres,
            @Param("now")    Instant                now,
            @Param("status") EventStatus            status,
            Pageable                                pageable
    );
}

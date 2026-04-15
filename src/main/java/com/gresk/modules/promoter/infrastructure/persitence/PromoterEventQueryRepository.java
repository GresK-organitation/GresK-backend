package com.gresk.modules.promoter.infrastructure.persitence;

import com.gresk.modules.event.infrastructure.persistence.EventEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

/**
 * Repositorio de solo lectura para obtener los eventos de un promotor.
 * Vive en el módulo Promoter para evitar dependencias inversas hacia
 * el módulo Event. Reutiliza EventEntity directamente sobre la tabla events.
 */
public interface PromoterEventQueryRepository extends Repository<EventEntity, UUID> {

    /**
     * Devuelve todos los eventos de un promotor ordenados por fecha descendente.
     */
    @Query("SELECT e FROM EventEntity e WHERE e.promoterId = :promoterId ORDER BY e.eventDate DESC")
    List<EventEntity> findAllByPromoterId(@Param("promoterId") UUID promoterId);
}

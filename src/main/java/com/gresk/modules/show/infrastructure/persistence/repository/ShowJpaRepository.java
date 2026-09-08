package com.gresk.modules.show.infrastructure.persistence.repository;

import com.gresk.modules.show.infrastructure.persistence.entity.ShowEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public interface ShowJpaRepository extends JpaRepository<ShowEntity, UUID> {

    List<ShowEntity> findByPromoterId(UUID promoterId);

    List<ShowEntity> findByPromoterIdAndStatus(UUID promoterId, String status);

    @Query("SELECT s FROM ShowEntity s WHERE s.status = 'OPCION_HOLD' AND s.holdExpiresAt <= :now")
    List<ShowEntity> findExpirableHolds(@Param("now") Instant now);
}

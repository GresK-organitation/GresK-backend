package com.gresk.modules.promoter.infrastructure.persitence;

import com.gresk.shared.domain.MusicGenre;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PromoterJpaRepository extends JpaRepository<PromoterEntity, UUID> {

    Optional<PromoterEntity> findByEmail(String email);

    boolean existsByEmail(String email);

    @Query("SELECT p FROM PromoterEntity p JOIN p.genres g WHERE g = :genre")
    List<PromoterEntity> findByGenresContaining(@Param("genre") MusicGenre genre);
}

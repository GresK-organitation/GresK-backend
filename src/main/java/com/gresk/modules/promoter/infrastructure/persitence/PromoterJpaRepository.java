package com.gresk.modules.promoter.infrastructure.persitence;

import com.gresk.modules.promoter.domain.MusicGenre;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PromoterJpaRepository extends JpaRepository<PromoterEntity, UUID> {

    Optional<PromoterEntity> findByEmail(String email);

    Optional<PromoterEntity> findByAccountId(UUID accountId);

    boolean existsByEmail(String email);

    List<PromoterEntity> findByActiveTrue();

    List<PromoterEntity> findByGenresContaining(MusicGenre genre);
}

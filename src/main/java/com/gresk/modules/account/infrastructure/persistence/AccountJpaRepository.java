package com.gresk.modules.account.infrastructure.persistence;

import com.gresk.modules.promoter.infrastructure.persitence.PromoterEntity;
import com.gresk.modules.user.infrastructure.persistence.entity.UserEntity;
import com.gresk.shared.domain.AccountStatus;
import com.gresk.shared.domain.Role;
import com.gresk.shared.domain.valueobject.City;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AccountJpaRepository extends JpaRepository<AccountEntity, UUID> {

    Optional<AccountEntity> findByEmail(String email);

    boolean existsByEmail(String email);

    boolean existsByRolesContaining(Role role);

    @Query("""
            SELECT u FROM UserEntity u
            JOIN AccountEntity a ON a.id = u.id
            WHERE (:status IS NULL OR a.status = :status)
            AND (:city IS NULL OR LOWER(u.city) = LOWER(:city))
            """)
    List<UserEntity> findUsersForAdmin(
            @Param("status") AccountStatus status,
            @Param("city") String city

    );

    @Query("""
            SELECT p FROM PromoterEntity p
            JOIN AccountEntity a ON a.id = p.id
            WHERE (:status IS NULL OR a.status = :status)
            AND (:city IS NULL OR LOWER(p.city) = LOWER(:city))
            """)
    List<PromoterEntity> findPromotersForAdmin(
            @Param("status") AccountStatus status,
            @Param("city") String city
    );
}

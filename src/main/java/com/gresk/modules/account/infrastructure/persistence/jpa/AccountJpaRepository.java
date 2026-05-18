package com.gresk.modules.account.infrastructure.persistence.jpa;

import com.gresk.modules.account.infrastructure.persistence.AccountEntity;
import com.gresk.shared.domain.AccountStatus;
import com.gresk.shared.domain.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface AccountJpaRepository extends JpaRepository<AccountEntity, UUID> {

    Optional<AccountEntity> findByEmail(String email);

    boolean existsByEmail(String email);

    boolean existsByRolesContaining(Role role);

    @Query(""" 
            SELECT a.status
            FROM AccountEntity a
            WHERE a.id = :id
            """)
    Optional<AccountStatus> findStatusById(
            @Param("id") UUID id
    );
}

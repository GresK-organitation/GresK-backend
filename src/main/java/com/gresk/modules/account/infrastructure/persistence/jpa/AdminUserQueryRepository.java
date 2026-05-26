package com.gresk.modules.account.infrastructure.persistence.jpa;

import com.gresk.modules.account.infrastructure.web.queries.AccountsAdminDTO;
import com.gresk.modules.user.infrastructure.persistence.entity.UserEntity;
import com.gresk.shared.domain.AccountStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface AdminUserQueryRepository extends JpaRepository<UserEntity, UUID> {
    @Query("""
                SELECT new com.gresk.modules.account.infrastructure.web.queries.AccountsAdminDTO(
                    u.id, u.name, u.email, u.city, a.status, u.createdAt
                )
                FROM UserEntity u
                JOIN AccountEntity a ON u.accountId = a.id
                WHERE (:status IS NULL OR a.status = :status)
                AND (:city IS NULL OR u.city = :city)
            """)
    Page<AccountsAdminDTO> findForAdmin(
            @Param("status") AccountStatus status,
            @Param("city") String city,
            Pageable pageable
    );
}

package com.gresk.modules.account.infrastructure.persistence.jpa;

import com.gresk.modules.account.infrastructure.web.queries.AccountsAdminDTO;
import com.gresk.modules.promoter.infrastructure.persitence.PromoterEntity;
import com.gresk.shared.domain.AccountStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface AdminPromoterQueryRepository extends JpaRepository<PromoterEntity, UUID> {
    @Query("""
                SELECT new com.gresk.modules.account.infrastructure.web.queries.AccountsAdminDTO(
                    p.id,
                    p.name,
                    p.email,
                    p.city,
                    a.status,
                    p.createdAt
                )
                FROM PromoterEntity p
                JOIN AccountEntity a ON p.accountId = a.id
                WHERE (:status IS NULL OR a.status = :status)
                AND (:city IS NULL OR p.city = :city)
            """)
    Page<AccountsAdminDTO> findForAdmin(
            @Param("status") AccountStatus status,
            @Param("city") String city,
            Pageable pageable
    );
}

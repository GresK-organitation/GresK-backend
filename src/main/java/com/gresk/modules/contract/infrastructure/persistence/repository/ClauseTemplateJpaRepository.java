package com.gresk.modules.contract.infrastructure.persistence.repository;

import com.gresk.modules.contract.infrastructure.persistence.entity.ClauseTemplateEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface ClauseTemplateJpaRepository extends JpaRepository<ClauseTemplateEntity, UUID> {

    @Query(value = """
            SELECT * FROM clause_templates
            WHERE system_default = true AND active = true
              AND applicable_types @> CAST('["' || :type || '"]' AS jsonb)
            ORDER BY code ASC
            """, nativeQuery = true)
    List<ClauseTemplateEntity> findSystemDefaultsByType(@Param("type") String type);

    List<ClauseTemplateEntity> findByPromoterId(UUID promoterId);
}

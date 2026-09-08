package com.gresk.modules.contract.domain.port.out;

import com.gresk.modules.contract.domain.model.ClauseTemplate;
import com.gresk.modules.contract.domain.model.ClauseTemplateId;
import com.gresk.modules.contract.domain.model.ContractType;
import com.gresk.modules.promoter.domain.model.valueobject.PromoterId;

import java.util.List;
import java.util.Optional;

public interface ClauseTemplateRepositoryPort {
    ClauseTemplate           save(ClauseTemplate template);
    Optional<ClauseTemplate> findById(ClauseTemplateId id);

    /** Ordenadas por code (asc) — el orden de la lista se usa como ContractClause.order. */
    List<ClauseTemplate>     findSystemDefaultsByType(ContractType type);
    List<ClauseTemplate>     findByPromoterId(PromoterId promoterId);
}

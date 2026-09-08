package com.gresk.modules.contract.domain.service;

import com.gresk.modules.contract.domain.model.ClauseTemplate;
import com.gresk.modules.contract.domain.model.Contract;
import com.gresk.modules.contract.domain.model.ContractType;
import com.gresk.modules.contract.domain.model.valueobject.ContractClause;
import com.gresk.modules.contract.domain.model.valueobject.ContractParty;
import com.gresk.modules.contract.domain.port.out.ClauseTemplateRepositoryPort;
import com.gresk.modules.promoter.domain.model.valueobject.PromoterId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Delega en el catálogo reutilizable de cláusulas (ClauseTemplateRepositoryPort,
 * poblado por V31__seed_system_clause_templates.sql) en vez de hardcodear el texto
 * legal aquí. ContractClause aplicado a un contrato es un snapshot congelado: si el
 * catálogo cambia después, los contratos ya creados no se ven afectados.
 */
@Component
@RequiredArgsConstructor
public class ContractTemplateFactory {

    private final ClauseTemplateRepositoryPort clauseTemplateRepository;

    public Contract createFromTemplate(ContractType type, PromoterId promoterId,
                                        ContractParty partyA, String referenceNumber) {
        Contract contract = Contract.create(type, promoterId, partyA, referenceNumber);
        return contract.withClauses(buildClauses(type));
    }

    private List<ContractClause> buildClauses(ContractType type) {
        List<ClauseTemplate> templates = clauseTemplateRepository.findSystemDefaultsByType(type);
        return java.util.stream.IntStream.range(0, templates.size())
                .mapToObj(i -> new ContractClause(i + 1, templates.get(i).getTitle(), templates.get(i).getContentTemplate()))
                .toList();
    }
}

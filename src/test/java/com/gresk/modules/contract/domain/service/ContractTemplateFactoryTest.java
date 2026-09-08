package com.gresk.modules.contract.domain.service;

import com.gresk.modules.contract.domain.model.ClauseTemplate;
import com.gresk.modules.contract.domain.model.Contract;
import com.gresk.modules.contract.domain.model.ContractType;
import com.gresk.modules.contract.domain.model.valueobject.ClauseCategory;
import com.gresk.modules.contract.domain.model.valueobject.ContractParty;
import com.gresk.modules.contract.domain.port.out.ClauseTemplateRepositoryPort;
import com.gresk.modules.promoter.domain.model.valueobject.PromoterId;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ContractTemplateFactoryTest {

    @Mock
    private ClauseTemplateRepositoryPort clauseTemplateRepository;

    @Test
    void mapeaLasPlantillasDelCatalogoAContractClauseEnOrden() {
        ClauseTemplate first  = ClauseTemplate.createCustom(null, "SYS-PERFORMANCE-01",
                ClauseCategory.CUSTOM, "Objeto", "Contenido 1", List.of(ContractType.PERFORMANCE), "ES");
        ClauseTemplate second = ClauseTemplate.createCustom(null, "SYS-PERFORMANCE-02",
                ClauseCategory.PAYMENT, "Honorarios", "Contenido 2", List.of(ContractType.PERFORMANCE), "ES");

        when(clauseTemplateRepository.findSystemDefaultsByType(ContractType.PERFORMANCE))
                .thenReturn(List.of(first, second));

        ContractTemplateFactory factory = new ContractTemplateFactory(clauseTemplateRepository);
        ContractParty partyA = new ContractParty("Promotora", "B1", "addr", "sig", "role", "email@test.com");
        PromoterId promoterId = PromoterId.of(UUID.randomUUID());

        Contract contract = factory.createFromTemplate(ContractType.PERFORMANCE, promoterId, partyA, "GRK-2026-001");

        assertEquals(2, contract.getClauses().size());
        assertEquals(1, contract.getClauses().get(0).order());
        assertEquals("Objeto", contract.getClauses().get(0).title());
        assertEquals(2, contract.getClauses().get(1).order());
        assertEquals("Honorarios", contract.getClauses().get(1).title());
    }
}

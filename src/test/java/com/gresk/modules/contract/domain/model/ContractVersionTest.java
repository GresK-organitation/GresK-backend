package com.gresk.modules.contract.domain.model;

import com.gresk.modules.contract.domain.model.valueobject.ContractParty;
import com.gresk.modules.contract.domain.model.valueobject.FinancialTerms;
import com.gresk.modules.promoter.domain.model.valueobject.PromoterId;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class ContractVersionTest {

    private final PromoterId promoterId = PromoterId.of(UUID.randomUUID());

    private ContractParty party(String name) {
        return new ContractParty(name, "B1", "addr", "sig", "role", "email@test.com");
    }

    @Test
    void snapshotCapturaEstadoInmutableIndependienteDeMutacionesPosteriores() {
        Contract contract = Contract.create(ContractType.PERFORMANCE, promoterId, party("Original"), "GRK-2026-001");
        contract.withFinancialTerms(new FinancialTerms(BigDecimal.TEN, "EUR", java.util.List.of()));

        ContractVersion version = ContractVersion.snapshot(contract, "primer borrador", promoterId.toString());
        assertEquals(2, version.getVersionNumber());
        assertEquals("Original", version.getPartyA().name());
        assertEquals(BigDecimal.TEN, version.getFinancialTerms().feeAmount());

        // Mutar el contrato original DESPUÉS del snapshot no debe afectar la versión ya tomada.
        contract.withPartyA(party("Modificado"));
        contract.withFinancialTerms(new FinancialTerms(BigDecimal.valueOf(999), "EUR", java.util.List.of()));

        assertEquals("Original", version.getPartyA().name());
        assertNotEquals(BigDecimal.valueOf(999), version.getFinancialTerms().feeAmount());
    }

    @Test
    void markSupersededCambiaElEstado() {
        Contract contract = Contract.create(ContractType.PERFORMANCE, promoterId, party("A"), "GRK-2026-001");
        ContractVersion version = ContractVersion.snapshot(contract, "v1", promoterId.toString());
        assertEquals(ContractVersionStatus.CURRENT, version.getStatus());

        version.markSuperseded();
        assertEquals(ContractVersionStatus.SUPERSEDED, version.getStatus());
    }
}

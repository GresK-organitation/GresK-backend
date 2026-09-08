package com.gresk.modules.contract.domain.model;

import com.gresk.modules.contract.domain.exception.InvalidContractStatusTransitionException;
import com.gresk.modules.contract.domain.model.valueobject.ContractParty;
import com.gresk.modules.promoter.domain.model.valueobject.PromoterId;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class ContractTest {

    private final PromoterId promoterId = PromoterId.of(UUID.randomUUID());

    private ContractParty party() {
        return new ContractParty("Barcelona Live", "B12345678", "Carrer Fake 1",
                "Ana Test", "Manager", "ana@test.com");
    }

    private Contract newDraft() {
        return Contract.create(ContractType.PERFORMANCE, promoterId, party(), "GRK-2026-001");
    }

    @Test
    void seCreaEnDraft() {
        Contract contract = newDraft();
        assertEquals(ContractStatus.DRAFT, contract.getStatus());
        assertEquals(1, contract.getCurrentVersionNumber());
        assertNull(contract.getActiveSignatureEnvelopeId());
    }

    @Test
    void sendPasaDeDraftASent() {
        Contract contract = newDraft();
        contract.send();
        assertEquals(ContractStatus.SENT, contract.getStatus());
    }

    @Test
    void markDeliveredExigeEstadoSent() {
        Contract contract = newDraft();
        assertThrows(InvalidContractStatusTransitionException.class, contract::markDelivered);

        contract.send();
        contract.markDelivered();
        assertEquals(ContractStatus.DELIVERED, contract.getStatus());
    }

    @Test
    void signFuncionaDesdeSentYDesdeDelivered() {
        Contract fromSent = newDraft();
        fromSent.send();
        fromSent.sign();
        assertEquals(ContractStatus.SIGNED, fromSent.getStatus());

        Contract fromDelivered = newDraft();
        fromDelivered.send();
        fromDelivered.markDelivered();
        fromDelivered.sign();
        assertEquals(ContractStatus.SIGNED, fromDelivered.getStatus());
    }

    @Test
    void signRechazaDesdeDraft() {
        Contract contract = newDraft();
        assertThrows(InvalidContractStatusTransitionException.class, contract::sign);
    }

    @Test
    void voidContractSoloDesdeSentODelivered() {
        Contract contract = newDraft();
        assertThrows(InvalidContractStatusTransitionException.class, contract::voidContract);

        contract.send();
        contract.voidContract();
        assertEquals(ContractStatus.VOIDED, contract.getStatus());
    }

    @Test
    void cancelRechazaDesdeVoided() {
        Contract contract = newDraft();
        contract.send();
        contract.voidContract();
        assertThrows(InvalidContractStatusTransitionException.class, contract::cancel);
    }

    @Test
    void advanceVersionIncrementaYNextVersionNumberNoMuta() {
        Contract contract = newDraft();
        assertEquals(2, contract.nextVersionNumber());
        assertEquals(1, contract.getCurrentVersionNumber());

        contract.advanceVersion();
        assertEquals(2, contract.getCurrentVersionNumber());
    }

    @Test
    void attachSignatureEnvelopeGuardaLaReferencia() {
        Contract contract = newDraft();
        SignatureEnvelopeId envelopeId = SignatureEnvelopeId.generate();
        contract.attachSignatureEnvelope(envelopeId);
        assertEquals(envelopeId, contract.getActiveSignatureEnvelopeId());
    }
}

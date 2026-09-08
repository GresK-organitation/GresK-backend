package com.gresk.modules.contract.domain.model;

import com.gresk.modules.contract.domain.exception.InvalidSignatureEnvelopeTransitionException;
import com.gresk.modules.contract.domain.model.valueobject.EnvelopeProvider;
import com.gresk.modules.contract.domain.model.valueobject.EnvelopeStatus;
import com.gresk.modules.contract.domain.model.valueobject.Signer;
import com.gresk.modules.contract.domain.model.valueobject.SignerRole;
import com.gresk.modules.contract.domain.model.valueobject.SignerStatus;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SignatureEnvelopeTest {

    private final ContractId contractId = ContractId.generate();

    private List<Signer> twoSigners() {
        return List.of(
                Signer.pending("signer-1", SignerRole.PROMOTER, "Ana", "ana@test.com", 1),
                Signer.pending("signer-2", SignerRole.ARTIST, "Bruno", "bruno@test.com", 2)
        );
    }

    @Test
    void seCreaEnDraft() {
        SignatureEnvelope envelope = SignatureEnvelope.create(contractId, EnvelopeProvider.MANUAL, twoSigners(), "hash");
        assertEquals(EnvelopeStatus.DRAFT, envelope.getStatus());
        assertFalse(envelope.isFullySigned());
    }

    @Test
    void markSentExigeDraft() {
        SignatureEnvelope envelope = SignatureEnvelope.create(contractId, EnvelopeProvider.MANUAL, twoSigners(), "hash");
        envelope.markSent("PROV-1");
        assertEquals(EnvelopeStatus.SENT, envelope.getStatus());
        assertNotNull(envelope.getSentAt());

        assertThrows(InvalidSignatureEnvelopeTransitionException.class, () -> envelope.markSent("PROV-2"));
    }

    @Test
    void seCompletaCuandoTodosLosFirmantesFirman() {
        SignatureEnvelope envelope = SignatureEnvelope.create(contractId, EnvelopeProvider.MANUAL, twoSigners(), "hash");
        envelope.markSent("PROV-1");

        envelope.markSignerSigned("signer-1");
        assertEquals(EnvelopeStatus.SENT, envelope.getStatus());
        assertFalse(envelope.isFullySigned());

        envelope.markSignerSigned("signer-2");
        assertTrue(envelope.isFullySigned());
        assertEquals(EnvelopeStatus.SIGNED, envelope.getStatus());
        assertNotNull(envelope.getCompletedAt());
        assertTrue(envelope.getSigners().stream().allMatch(s -> s.status() == SignerStatus.SIGNED));
    }

    @Test
    void markDeclinedTerminaElEnvelope() {
        SignatureEnvelope envelope = SignatureEnvelope.create(contractId, EnvelopeProvider.MANUAL, twoSigners(), "hash");
        envelope.markSent("PROV-1");
        envelope.markDeclined("signer-1");
        assertEquals(EnvelopeStatus.DECLINED, envelope.getStatus());

        assertThrows(InvalidSignatureEnvelopeTransitionException.class, () -> envelope.markSignerSigned("signer-2"));
    }

    @Test
    void noSePuedeAnularUnEnvelopeYaFirmado() {
        SignatureEnvelope envelope = SignatureEnvelope.create(contractId, EnvelopeProvider.MANUAL, twoSigners(), "hash");
        envelope.markSent("PROV-1");
        envelope.markSignerSigned("signer-1");
        envelope.markSignerSigned("signer-2");

        assertThrows(InvalidSignatureEnvelopeTransitionException.class, envelope::markVoided);
    }
}

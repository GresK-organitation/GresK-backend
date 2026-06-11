package com.gresk.modules.email.domain.port.out;

import com.gresk.modules.email.domain.model.EmailMessage;
import com.gresk.modules.email.domain.model.EmailMessageId;
import com.gresk.modules.promoter.domain.model.valueobject.PromoterId;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface EmailMessageRepositoryPort {
    EmailMessage           save(EmailMessage message);
    Optional<EmailMessage> findById(EmailMessageId id);
    Optional<EmailMessage> findByExternalMessageId(String externalMessageId);
    List<EmailMessage>     findByPromoterId(PromoterId promoterId);
    List<EmailMessage>     findByEventIdAndPromoterId(UUID eventId, PromoterId promoterId);
    List<EmailMessage>     findByEventIdAndPromoterId(UUID eventId, PromoterId promoterId, int page, int size);
    boolean                existsByExternalMessageId(String externalMessageId);

    /** FAILED con intentos restantes: candidatos a reintento. */
    List<EmailMessage>     findFailedWithAttemptsLessThan(int maxAttempts);

    /** FAILED con los intentos agotados: candidatos a DEAD_LETTER. */
    List<EmailMessage>     findFailedWithAttemptsAtLeast(int maxAttempts);
}
